package org.hydra.interpreter.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CallExpression implements Expression {
    private final Expression function;
    private final List<Expression> arguments = new ArrayList<>();

    public CallExpression(Expression function) {
        this.function = function;
    }

    public Expression getFunction() {
        return function;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public String tokenLiteral() {
        return "Call expression";
    }

    public String toString() {
        return function.toString() + "(" + arguments.stream().map(a -> a.toString()).collect(Collectors.joining(", ")) + ")";
    }
}
