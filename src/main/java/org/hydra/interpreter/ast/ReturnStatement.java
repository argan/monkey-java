package org.hydra.interpreter.ast;

import org.hydra.interpreter.token.TokenType;

public class ReturnStatement implements Statement {
    private final Expression value;

    public ReturnStatement(Expression value) {
        this.value = value;
    }

    public Expression getReturnValue() {
        return value;
    }

    @Override
    public String tokenLiteral() {
        return TokenType.RETURN.getLiteral();
    }

    public String toString() {
        return "return " + value.toString()+";";
    }
}
