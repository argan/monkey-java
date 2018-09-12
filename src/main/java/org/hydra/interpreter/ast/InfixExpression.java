package org.hydra.interpreter.ast;

public class InfixExpression implements Expression {
    private String operator;
    private Expression left;
    private Expression right;

    public InfixExpression(String operator, Expression left, Expression right) {
        this.operator = operator;
        this.right = right;
        this.left = left;
    }

    public String getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
    }

    public Expression getLeft() {
        return left;
    }

    @Override
    public String tokenLiteral() {
        return operator;
    }

    public String toString() {
        return "(" + left.toString() + " " + operator + " " + right.toString() + ")";
    }
}
