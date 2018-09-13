package org.hydra.interpreter.ast;

public class Identifier implements Expression {
    private final String value;

    public Identifier(String v) {
        this.value = v;
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
