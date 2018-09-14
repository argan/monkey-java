package org.hydra.interpreter.ast;

public class IndexExpression implements Expression {
    private final Expression left, index;

    public IndexExpression(Expression left, Expression index) {
        this.left = left;
        this.index = index;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getIndex() {
        return index;
    }

    @Override
    public String tokenLiteral() {
        return "Index Expression";
    }

    @Override
    public String toString() {
        return String.format("(%s[%s])", left.toString(), index.toString());
    }
}
