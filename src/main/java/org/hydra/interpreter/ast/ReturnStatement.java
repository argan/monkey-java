package org.hydra.interpreter.ast;

import org.hydra.interpreter.token.TokenType;

public class LetStatement implements Statement {
    private final Expression value;
    private final Idenifier identifier;

    public LetStatement(Idenifier identifier, Expression value) {
        this.identifier = identifier;
        this.value = value;
    }

    public Expression getValue() {
        return value;
    }

    public Idenifier getIdentifier() {
        return identifier;
    }

    @Override
    public String tokenLiteral() {
        return TokenType.LET.getLiteral();
    }
}
