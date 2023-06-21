package com.guflimc.multicore.common;

import com.guflimc.multicore.MultiStorage;
import com.guflimc.multicore.Subscription;
import com.guflimc.multicore.common.subscription.SubscriptionAttribute;
import com.guflimc.multicore.attribute.AttributeKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

public abstract class AbstractMultiStorage implements MultiStorage {

    private final Map<AttributeKey<?>, Serializable> attributes = new ConcurrentHashMap<>();
    private final Collection<SubscriptionAttribute<?>> subscriptions = new CopyOnWriteArraySet<>();

    //

    @Override
    public <T extends Serializable> Optional<T> attribute(@NotNull AttributeKey<T> key) {
        return Optional.ofNullable(key.type().cast(attributes.get(key)));
    }

    @Override
    public <T extends Serializable> void setAttribute(@NotNull AttributeKey<T> key, @NotNull T value) {
        update(key, value);
        sync(key, value);
    }

    @Override
    public <T extends Serializable> void removeAttribute(@NotNull AttributeKey<T> key) {
        update(key, null);
        sync(key, null);
    }

    protected abstract <T extends Serializable> void sync(@NotNull AttributeKey<T> key, @Nullable T value);

    @Override
    public <T extends Serializable> Subscription subscribe(@NotNull AttributeKey<T> key, @NotNull Consumer<T> callback) {
        SubscriptionAttribute<T> sub = new SubscriptionAttribute<>(key, callback) {
            @Override
            public void unsubscribe() {
                subscriptions.remove(this);
            }
        };
        subscriptions.add(sub);
        return sub;
    }

    //

    @ApiStatus.Internal
    public void update(@NotNull AttributeKey<?> key, @Nullable Serializable value) {
        if ( value == null) {
            attributes.remove(key);
            return;
        }

        attributes.put(key, value);
        subscriptions.forEach(sub -> sub.handle(key, value));
    }
}
