package org.hydra.interpreter.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArrayLiteral implements Expression {
    private final List<Expression> elements;

    public ArrayLiteral(List<Expression> elements) {
        this.elements = elements;
    }

    public List<Expression> getElements() {
        return elements;
    }

    @Override
    public String tokenLiteral() {
        return "Array Literal";
    }

    @Override
    public String toString() {
        return elements.stream().map(e -> e.toString()).collect(Collectors.joining(", ", "[", "]"));
    }
}
