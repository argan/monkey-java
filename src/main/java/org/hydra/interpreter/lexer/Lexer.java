package org.hydra.interpreter.lexer;

import org.hydra.interpreter.token.Keywords;
import org.hydra.interpreter.token.Token;
import org.hydra.interpreter.token.TokenType;

public class Lexer {

    private char[] input;
    private int position, readPosition;
    private char ch;

    public Lexer(String input) {
        this.input = input.toCharArray();
        readChar();
    }

    public Token nextToken() {
        Token token;
        skipWhiteSpace();
        switch (ch) {
            case '+':
                token = new Token(TokenType.PLUS);
                break;
            case '-':
                token = new Token(TokenType.MINUS);
                break;
            case '*':
                token = new Token(TokenType.MULTI);
                break;
            case '/':
                token = new Token(TokenType.DIVIDE);
                break;

            case '{':
                token = new Token(TokenType.LBRACE);
                break;
            case '}':
                token = new Token(TokenType.RBRACE);
                break;
            case '[':
                token = new Token(TokenType.LBRACKT);
                break;
            case ']':
                token = new Token(TokenType.RBRACKET);
                break;
            case '(':
                token = new Token(TokenType.LPAREN);
                break;
            case ')':
                token = new Token(TokenType.RPAREN);
                break;
            case ',':
                token = new Token(TokenType.COMMA);
                break;
            case ';':
                token = new Token(TokenType.SEMICOLON);
                break;
            case ':':
                token = new Token(TokenType.COLON);
                break;
            case '>':
                if (peekChar() == '=') {
                    readChar();
                    token = new Token(TokenType.GTE, ">=");
                } else {
                    token = new Token(TokenType.GT);
                }
                break;
            case '<':
                if (peekChar() == '=') {
                    readChar();
                    token = new Token(TokenType.LTE, "<=");
                } else {
                    token = new Token(TokenType.LT);
                }
                break;
            case '!':
                if (peekChar() == '=') {
                    readChar();
                    token = new Token(TokenType.NE, "!=");
                } else {
                    token = new Token(TokenType.BANG);
                }
                break;
            case '=':
                if (peekChar() == '=') {
                    readChar();
                    token = new Token(TokenType.EQ, "==");
                } else {
                    token = new Token(TokenType.ASSIGN);
                }
                break;
            case 0:
                token = new Token(TokenType.EOF);
                break;
            default:
                if (isLetter(ch)) {
                    String identifier = readIdentifier();
                    return new Token(Keywords.tokenType(identifier), identifier);
                } else if (isDigit(ch)) {
                    String num = readNumber();
                    return new Token(TokenType.INTEGER, num);
                }
                token = new Token(TokenType.ILLEGAL, String.valueOf(ch));
        }
        readChar();
        return token;
    }

    private String readIdentifier() {
        int current = position;
        int cnt = 0;
        while (isLetter(ch)) {
            readChar();
            cnt++;
        }
        return new String(input, current, cnt);
    }

    private String readNumber() {
        int current = position;
        int cnt = 0;
        while (isDigit(ch)) {
            readChar();
            cnt++;
        }
        return new String(input, current, cnt);
    }

    private char peekChar() {
        if (position >= input.length) {
            return 0;
        }
        return input[readPosition];
    }

    private void readChar() {
        if (readPosition >= input.length) {
            this.ch = 0;
        } else {
            this.ch = input[readPosition];
        }
        position = readPosition;
        readPosition++;
    }

    private boolean isLetter(char c) {
        return c == '_' || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void skipWhiteSpace() {
        while (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n') {
            readChar();
        }
    }
}
