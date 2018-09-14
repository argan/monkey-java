package org.hydra.interpreter.object;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hydra.interpreter.object.MNull.NULL;

public class MHash implements MObject {
    private final Map<HashKey, HashPair> table = new HashMap<>();

    @Override
    public ObjectType type() {
        return ObjectType.HASH_OBJ;
    }

    public void put(MObject key, MObject value) {
        table.put(((Hashable) key).hashKey(), new HashPair(key, value));
    }

    public MObject get(Hashable key) {
        HashPair pair = table.get(key.hashKey());
        if (pair != null) {
            return pair.getValue();
        }
        return NULL;
    }

    @Override
    public String toString() {
        return table.values().stream().map(p -> p.getKey().toString() + ":" + p.getValue().toString())
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
