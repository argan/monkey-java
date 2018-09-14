package org.hydra.interpreter.object;

public class MString implements MObject {
    private final String value;

    public String getValue() {
        return value;
    }

    public MString(String value) {
        this.value = value;
    }

    @Override
    public ObjectType type() {
        return ObjectType.STRING_OBJ;
    }

    public String toString() {
        return value;
    }
}
