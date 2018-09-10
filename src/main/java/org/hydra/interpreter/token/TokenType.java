package org.hydra.interpreter.token;

public enum TokenType {
    ILLEGAL("IGGEGAL"), EOF("EOF"),
    IDENTIFIER("IDENTIFIER"), INTEGER("INTEGER"),
    ASSIGN("="),
    PLUS("+"), MINUS("-"), MULTI("*"), DIVIDE("/"),
    COMMA(","), SEMICOLON(";"), COLON(":"),
    LPAREN("("), RPAREN(")"), LBRACE("{"), RBRACE("}"), LBRACKT("["), RBRACKET("]"),
    FUNCTION("fn"), LET("let");

    private String literal;

    TokenType(String token) {
        this.literal = token;
    }

    public String getLiteral() {
        return literal;
    }
}
