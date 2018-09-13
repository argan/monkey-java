package org.hydra.interpreter.object;

public class MReturnValue implements MObject {
    private final MObject value;

    public MReturnValue(MObject value) {
        this.value = value;
    }

    public MObject getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public ObjectType type() {
        return ObjectType.RETURN_OBJ;
    }
}
