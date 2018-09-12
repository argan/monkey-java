package org.hydra.interpreter.ast;

public class IfExpression implements Expression {
    private final Expression condition;
    private final BlockStatement consequence, alternative;

    public IfExpression(Expression condition, BlockStatement consequence, BlockStatement alternative) {
        this.condition = condition;
        this.consequence = consequence;
        this.alternative = alternative;
    }

    @Override
    public String tokenLiteral() {
        return "if";
    }

    public Expression getCondition() {
        return condition;
    }

    public BlockStatement getConsequence() {
        return consequence;
    }

    public BlockStatement getAlternative() {
        return alternative;
    }

    @Override
    public String toString() {
        String s = "if " + condition + " " + consequence;
        if (alternative != null) {
            s += " else " + alternative;
        }
        return s;
    }
}
