package org.hydra.interpreter.object;

import java.util.List;
import java.util.stream.Collectors;

public class MArray implements MObject {
    private final List<MObject> elements;

    public MArray(List<MObject> elements) {
        this.elements = elements;
    }

    public List<MObject> getElements() {
        return elements;
    }

    @Override
    public ObjectType type() {
        return ObjectType.ARRAY_OBJ;
    }

    @Override
    public String toString() {
        return elements.stream().map(e -> e.toString()).collect(Collectors.joining(", ", "[", "]"));
    }
}
