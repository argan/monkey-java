package org.hydra.interpreter.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionLiteral implements Expression {
    private final List<Identifier> parameters = new ArrayList<>();
    private final BlockStatement body;

    public FunctionLiteral(BlockStatement body) {
        this.body = body;
    }

    public List<Identifier> getParameters() {
        return parameters;
    }

    public BlockStatement getBody() {
        return body;
    }

    @Override
    public String tokenLiteral() {
        return "fn";
    }

    @Override
    public String toString() {
        return "fn(" + parameters.stream().map(p -> p.getValue()).collect(Collectors.joining(", ")) +
                ") " + body;
    }

}
