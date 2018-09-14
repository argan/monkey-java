package org.hydra.interpreter.object;

public class MInteger implements MObject,Hashable {
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

    @Override
    public HashKey hashKey() {
        return new HashKey(ObjectType.INTEGER_OBJ, value);
    }
}
