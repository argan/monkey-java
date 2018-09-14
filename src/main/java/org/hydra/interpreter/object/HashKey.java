package org.hydra.interpreter.object;

public class HashKey {
    private final ObjectType type;
    private final long key;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HashKey hashKey = (HashKey) o;

        if (key != hashKey.key) return false;
        return type == hashKey.type;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (int) (key ^ (key >>> 32));
        return result;
    }

    public HashKey(ObjectType type, long key) {
        this.type = type;
        this.key = key;
    }
}
