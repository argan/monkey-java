package org.hydra.interpreter.ast;

public class PrefixExpression implements Expression {
    private String operator;
    private Expression right;

    public PrefixExpression(String operator, Expression right) {
        this.operator = operator;
        this.right = right;
    }

    public String getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public String tokenLiteral() {
        return operator;
    }

    public String toString() {
        return "(" + operator + right.toString() + ")";
    }
}
