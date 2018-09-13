package org.hydra.interpreter.object;

public class MError implements MObject {
    private final String message;

    public String getMessage() {
        return message;
    }

    public MError(String message) {
        this.message = message;
    }

    @Override
    public ObjectType type() {
        return ObjectType.ERROR_OBJ;
    }

    public String toString() {
        return "ERROR: " + message;
    }
}
