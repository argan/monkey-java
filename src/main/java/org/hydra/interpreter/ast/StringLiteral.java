package org.hydra.interpreter.ast;

public class StringLiteral implements Expression {
    private final String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String tokenLiteral() {
        return value;
    }

    public String toString() {
        return value;
    }
}
