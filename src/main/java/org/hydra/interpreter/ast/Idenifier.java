package org.hydra.interpreter.ast;

public class Idenifier implements Expression {
    private final String value;

    public Idenifier(String v) {
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
