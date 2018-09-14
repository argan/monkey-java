package org.hydra.interpreter.object;

public class HashPair {
    private final MObject key, value;

    public MObject getKey() {
        return key;
    }

    public MObject getValue() {
        return value;
    }

    public HashPair(MObject key, MObject value) {
        this.key = key;
        this.value = value;
    }
}
