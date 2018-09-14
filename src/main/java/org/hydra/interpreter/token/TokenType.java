package org.hydra.interpreter.token;

import org.hydra.interpreter.parser.Precedences;

import static org.hydra.interpreter.parser.Precedences.*;

public enum TokenType {
    ILLEGAL("ILLEGAL"), EOF("EOF"),
    IDENTIFIER("IDENTIFIER"), INTEGER("INTEGER"),STRING("STRING"),
    ASSIGN("="), BANG("!"),
    PLUS("+", Precedences.PLUS), MINUS("-", Precedences.PLUS), MULTI("*", MULTIPLY), DIVIDE("/", MULTIPLY),
    COMMA(","), SEMICOLON(";"), COLON(":"),
    LPAREN("(", CALL), RPAREN(")"), LBRACE("{"), RBRACE("}"), LBRACKT("["), RBRACKET("]"),
    GT(">", LESSGREATER), LT("<", LESSGREATER), GTE(">=", LESSGREATER), LTE("<=", LESSGREATER),
    EQ("==", EQUALS), NE("!=", EQUALS),
    FUNCTION("fn"), LET("let"), TRUE("true"), FALSE("false"), IF("if"), ELSE("else"), RETURN("return");

    private String literal;
    private Token token;
    private int precedence;

    TokenType(String t) {
        this(t, LOWEST);
    }

    TokenType(String t, int precedence) {
        this.literal = t;
        this.precedence = precedence;
        this.token = new Token(this);
    }

    public String getLiteral() {
        return literal;
    }

    public Token getToken() {
        return token;
    }

    public int getPrecedence() {
        return this.precedence;
    }
}
