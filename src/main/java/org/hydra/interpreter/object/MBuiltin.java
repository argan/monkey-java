package org.hydra.interpreter.object;

public class MBuiltin implements MObject {
    private final String name;
    private final BuiltinFunction func;

    public MBuiltin(String name, BuiltinFunction func) {
        this.func = func;
        this.name = name;
    }

    public BuiltinFunction getFunc() {
        return func;
    }

    public String getName() {
        return name;
    }

    @Override
    public ObjectType type() {
        return ObjectType.BUILTIN_OBJ;
    }

    @Override
    public String toString() {
        return "BuiltinFunction-" + name;
    }
}
