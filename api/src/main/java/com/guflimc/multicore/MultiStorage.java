package com.guflimc.multicore;

import com.guflimc.multicore.attribute.AttributeKey;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Consumer;

public interface MultiStorage {

    <T extends Serializable> Optional<T> attribute(@NotNull AttributeKey<T> key);

    <T extends Serializable> void setAttribute(@NotNull AttributeKey<T> key, @NotNull T value);

    <T extends Serializable> void removeAttribute(@NotNull AttributeKey<T> key);

    <T extends Serializable> Subscription subscribe(@NotNull AttributeKey<T> key, @NotNull Consumer<T> callback);

}
