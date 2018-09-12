package org.hydra.interpreter.token;

public class Token {
    private final TokenType type;
    private final String literal;

    public Token(TokenType type, String literal) {
        this.type = type;
        this.literal = literal;
    }

    public Token(TokenType type) {
        this.type = type;
        this.literal = type.getLiteral();
    }

    public TokenType getType() {
        return type;
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type.name() +
                ", literal='" + literal + '\'' +
                '}';
    }

    public int getPrecedence() {
        return this.type.getPrecedence();
    }
}
