package org.hydra.interpreter.ast;

public class IntegerLiteral implements Expression {
    private final int value;

    public IntegerLiteral(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String tokenLiteral() {
        return String.valueOf(value);
    }

    public String toString() {
        return String.valueOf(value);
    }
}
