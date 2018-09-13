package org.hydra.interpreter.object;

public class MNull implements MObject {

    public static final MNull NULL = new MNull();
    private MNull(){}

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public ObjectType type() {
        return ObjectType.NULL_OBJ;
    }
}
