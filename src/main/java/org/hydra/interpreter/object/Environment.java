package org.hydra.interpreter.object;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private Environment parent;
    private final Map<String, MObject> store = new HashMap<>();

    public Environment() {
        this(null);
    }

    public Environment(Environment p) {
        this.parent = p;
    }

    public MObject get(String name) {
        MObject obj = store.get(name);
        if (parent != null) {
            return parent.get(name);
        }
        return obj;
    }

    public MObject set(String name, MObject obj) {
        store.put(name, obj);
        return obj;
    }
}
