package org.hydra.interpreter.ast;

public class BooleanLiteral implements Expression {
    private final boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String tokenLiteral() {
        return String.valueOf(value);
    }

    public String toString() {
        return tokenLiteral();
    }
}
