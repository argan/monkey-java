package org.hydra.interpreter.parser;

import org.hydra.interpreter.ast.*;
import org.hydra.interpreter.lexer.Lexer;
import org.hydra.interpreter.token.Token;
import org.hydra.interpreter.token.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hydra.interpreter.parser.Precedences.*;

public class Parser {
    private final Lexer lexer;
    private Token current, peek;
    private List<String> errors;

    private Map<TokenType, PrefixParser> prefixParserMap;
    private Map<TokenType, InfixParser> infixParserMap;

    public Parser(Lexer l) {
        this.lexer = l;
        this.errors = new ArrayList<>();
        initParsers();
        nextToken();
        nextToken();
    }

    private void initParsers() {
        prefixParserMap = new HashMap<>();
        infixParserMap = new HashMap<>();

        prefixParserMap.put(TokenType.IDENTIFIER, this::parseIdentifier);
        prefixParserMap.put(TokenType.INTEGER, this::parseIntegerLiteral);
        prefixParserMap.put(TokenType.MINUS, this::parsePrefixExpression);
        prefixParserMap.put(TokenType.BANG, this::parsePrefixExpression);
        prefixParserMap.put(TokenType.TRUE, this::parseBooleanLiteral);
        prefixParserMap.put(TokenType.FALSE, this::parseBooleanLiteral);
        prefixParserMap.put(TokenType.LPAREN, this::parseGroupExpression);
        prefixParserMap.put(TokenType.IF, this::parseIfExpression);
        prefixParserMap.put(TokenType.FUNCTION, this::parseFunctionLiteral);
        prefixParserMap.put(TokenType.STRING, this::parseStringLiteral);
        prefixParserMap.put(TokenType.LBRACKET, this::parseArrayLiteral);
        prefixParserMap.put(TokenType.LBRACE, this::parseHashLiteral);

        infixParserMap.put(TokenType.PLUS, this::parseInfixExpression);
        infixParserMap.put(TokenType.MINUS, this::parseInfixExpression);
        infixParserMap.put(TokenType.MULTI, this::parseInfixExpression);
        infixParserMap.put(TokenType.DIVIDE, this::parseInfixExpression);
        infixParserMap.put(TokenType.GT, this::parseInfixExpression);
        infixParserMap.put(TokenType.LT, this::parseInfixExpression);
        infixParserMap.put(TokenType.GTE, this::parseInfixExpression);
        infixParserMap.put(TokenType.LTE, this::parseInfixExpression);
        infixParserMap.put(TokenType.EQ, this::parseInfixExpression);
        infixParserMap.put(TokenType.NE, this::parseInfixExpression);
        infixParserMap.put(TokenType.LPAREN, this::parseCallExpression);
        infixParserMap.put(TokenType.LBRACKET, this::parseIndexExpression);

    }

    private Expression parseStringLiteral() {
        return new StringLiteral(current.getLiteral());
    }

    public Program parseProgram() {
        Program program = new Program();
        while (current.getType() != TokenType.EOF) {
            Statement stmt = parseStatement();
            if (stmt != null) {
                program.getStatements().add(stmt);
            }
            nextToken();
        }
        return program;
    }

    private Statement parseStatement() {
        switch (current.getType()) {
            case LET:
                return parseLetStatement();
            case RETURN:
                return parseReturnStatement();
            default:
                return parseExpressionStatement();
        }
    }

    private Statement parseExpressionStatement() {
        Expression expression = parseExpression(LOWEST);
        if (peekIs(TokenType.SEMICOLON)) {
            nextToken();
        }
        return new ExpressionStatement(expression);
    }


    private Statement parseLetStatement() {
        if (!expectPeek(TokenType.IDENTIFIER)) {
            return null;
        }
        Identifier identifier = new Identifier(current.getLiteral());
        if (!expectPeek(TokenType.ASSIGN)) {
            return null;
        }
        nextToken();
        Expression exp = parseExpression(LOWEST);

        if (peekIs(TokenType.SEMICOLON)) {
            nextToken();
        }
        return new LetStatement(identifier, exp);
    }

    private Statement parseReturnStatement() {
        nextToken();
        Expression exp = parseExpression(LOWEST);

        if (peekIs(TokenType.SEMICOLON)) {
            nextToken();
        }

        return new ReturnStatement(exp);
    }

    private Expression parseIfExpression() {
        if (!expectPeek(TokenType.LPAREN)) {
            return null;
        }
        nextToken();

        Expression condition = parseExpression(LOWEST);

        if (!expectPeek(TokenType.RPAREN)) {
            return null;
        }
        if (!expectPeek(TokenType.LBRACE)) {
            return null;
        }

        BlockStatement consequence = parseBlockStatement();
        BlockStatement alternative = null;
        if (peekIs(TokenType.ELSE)) {
            nextToken();
            if (!expectPeek(TokenType.LBRACE)) {
                return null;
            }
            alternative = parseBlockStatement();
        }

        return new IfExpression(condition, consequence, alternative);

    }

    private BlockStatement parseBlockStatement() {
        nextToken();
        BlockStatement block = new BlockStatement();
        while (!currentIs(TokenType.RBRACE)) {
            Statement stmt = parseStatement();
            if (stmt != null) {
                block.getStatements().add(stmt);
            }
            nextToken();
        }
        return block;
    }


    private Expression parseExpression(int precedence) {
        PrefixParser prefix = prefixParserMap.get(current.getType());
        if (prefix == null) {
            errors.add("no prefix parser for " + current.getType().getLiteral());
            return null;
        }

        Expression left = prefix.parse();
        while (!peekIs(TokenType.SEMICOLON) && precedence < peekPrecedence()) {
            InfixParser infix = infixParserMap.get(peek.getType());
            if (infix == null) {
                return left;
            }
            nextToken();

            left = infix.parse(left);
        }
        return left;
    }
    private Expression parseHashLiteral() {
        Map<Expression,Expression> kvs = new HashMap<>();
        while(!peekIs(TokenType.RBRACE)) {
            nextToken();
            Expression key = parseExpression(LOWEST);
            if(!expectPeek(TokenType.COLON)) {
                return null;
            }
            nextToken();
            Expression value = parseExpression(LOWEST);
            kvs.put(key,value);

            if (!peekIs(TokenType.RBRACE) && !expectPeek(TokenType.COMMA)) {
                return null;
            }
        }
        if (!expectPeek(TokenType.RBRACE)) {
            return null;
        }
        return new HashLiteral(kvs);
    }
    private Expression parseFunctionLiteral() {

        if (!expectPeek(TokenType.LPAREN)) {
            return null;
        }

        List<Identifier> parameters = parseFunctionParameters();
        if (!expectPeek(TokenType.LBRACE)) {
            return null;
        }

        BlockStatement body = parseBlockStatement();
        FunctionLiteral fun = new FunctionLiteral(body);
        fun.getParameters().addAll(parameters);
        return fun;
    }

    private List<Identifier> parseFunctionParameters() {
        List<Identifier> result = new ArrayList<>();
        if (peekIs(TokenType.RPAREN)) {
            nextToken();
            return result;
        }
        nextToken();

        Identifier identifier = new Identifier(current.getLiteral());
        result.add(identifier);

        while (peekIs(TokenType.COMMA)) {
            nextToken();
            nextToken();
            result.add(new Identifier(current.getLiteral()));
        }

        if (!expectPeek(TokenType.RPAREN)) {
            return null;
        }

        return result;
    }

    private Expression parseGroupExpression() {
        nextToken();
        Expression exp = parseExpression(LOWEST);
        if (!expectPeek(TokenType.RPAREN)) {
            return null;
        }
        return exp;
    }

    private Expression parseIdentifier() {
        return new Identifier(current.getLiteral());
    }

    private Expression parseIntegerLiteral() {
        try {
            return new IntegerLiteral(Integer.parseInt(current.getLiteral()));
        } catch (Exception e) {
            errors.add(e.getMessage());
            return null;
        }
    }

    private Expression parseBooleanLiteral() {
        return new BooleanLiteral("true".equals(current.getLiteral()));
    }

    private Expression parsePrefixExpression() {
        String operator = current.getLiteral();
        nextToken();

        Expression exp = parseExpression(PREFIX);

        return new PrefixExpression(operator, exp);
    }

    private Expression parseCallExpression(Expression left) {
        List<Expression> arguments = parseExpressionList(TokenType.RPAREN);

        return new CallExpression(left, arguments);
    }

    private Expression parseIndexExpression(Expression left) {
        nextToken();
        Expression index = parseExpression(LOWEST);
        if (!expectPeek(TokenType.RBRACKET)) {
            return null;
        }
        return new IndexExpression(left, index);
    }

    private Expression parseArrayLiteral() {
        return new ArrayLiteral(parseExpressionList(TokenType.RBRACKET));
    }

    private List<Expression> parseExpressionList(TokenType end) {
        List<Expression> arguments = new ArrayList<>();
        if (peekIs(end)) {
            nextToken();
            return arguments;
        }
        nextToken();
        arguments.add(parseExpression(LOWEST));

        while (peekIs(TokenType.COMMA)) {
            nextToken();
            nextToken();

            arguments.add(parseExpression(LOWEST));
        }

        if (!expectPeek(end)) {
            return null;
        }

        return arguments;
    }

    private Expression parseInfixExpression(Expression left) {
        String operator = current.getLiteral();
        int precedence = currentPrecedence();

        nextToken();

        Expression right = parseExpression(precedence);

        return new InfixExpression(operator, left, right);
    }

    private void nextToken() {
        current = peek;
        peek = lexer.nextToken();
    }

    private boolean currentIs(TokenType t) {
        return current.getType() == t;
    }

    private boolean peekIs(TokenType t) {
        return peek.getType() == t;
    }

    private int currentPrecedence() {
        return current.getPrecedence();
    }

    private int peekPrecedence() {
        return peek.getPrecedence();
    }

    private boolean expectPeek(TokenType t) {
        if (peekIs(t)) {
            nextToken();
            return true;
        } else {
            peekError(t);
            return false;
        }
    }

    private void peekError(TokenType t) {
        this.errors.add(String.format("expected next token to be %s ,got %s", t.getLiteral(), peek.getType().getLiteral()));
    }

    public List<String> errors() {
        return this.errors;
    }
}
