package org.hydra.interpreter.object;

public class MBoolean implements MObject {

    public static final MBoolean TRUE = new MBoolean(true);
    public static final MBoolean FALSE = new MBoolean(false);

    private final boolean value;

    private MBoolean(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public ObjectType type() {
        return ObjectType.BOOLEAN_OBJ;
    }
}
