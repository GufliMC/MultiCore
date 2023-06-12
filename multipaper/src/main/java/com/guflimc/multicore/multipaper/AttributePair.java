package com.guflimc.multicore.multipaper;

import com.guflimc.multicore.attribute.AttributeKey;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

record AttributePair<T extends Serializable>(@NotNull AttributeKey<T> key, @NotNull T value) implements Serializable {

    public AttributePair(AttributeKey<?> key, Object value) {
        this((AttributeKey<T>) key, (T) value);
    }

}
