package org.hydra.interpreter.ast;

public class ExpressionStatement implements Statement {
    private final Expression expression;

    public ExpressionStatement(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public String tokenLiteral() {
        return "Express Statement";
    }

    public String toString() {
        return expression == null ? "" : expression.toString();
    }
}
