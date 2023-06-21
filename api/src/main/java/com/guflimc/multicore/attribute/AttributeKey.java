package com.guflimc.multicore.attribute;

import java.io.Serializable;
import java.util.Objects;

public class AttributeKey<T extends Serializable> implements Serializable {

    private final String id;
    private final Class<T> type;

    public AttributeKey(String id, Class<T> type) {
        this.id = id;
        this.type = type;
    }

    public String id() {
        return id;
    }

    public Class<T> type() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AttributeKey<?> ak && ak.id.equals(id) && ak.type.equals(type);
    }
}
