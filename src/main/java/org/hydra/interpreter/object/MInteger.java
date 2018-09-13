package org.hydra.interpreter.object;

public class MInteger implements MObject {
    private final int value;

    public MInteger(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public ObjectType type() {
        return ObjectType.INTEGER_OBJ;
    }
}
