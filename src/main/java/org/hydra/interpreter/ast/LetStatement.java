package org.hydra.interpreter.ast;

import org.hydra.interpreter.token.TokenType;

public class LetStatement implements Statement {
    private final Expression value;
    private final Identifier identifier;

    public LetStatement(Identifier identifier, Expression value) {
        this.identifier = identifier;
        this.value = value;
    }

    public Expression getValue() {
        return value;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public String tokenLiteral() {
        return TokenType.LET.getLiteral();
    }

    public String toString() {
        return "let " + identifier.toString() + " = " + value.toString() +";";
    }
}
