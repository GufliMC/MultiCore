package com.guflimc.multicore.common.subscription;

import com.guflimc.multicore.attribute.AttributeKey;

import java.io.Serializable;
import java.util.function.Consumer;

public abstract class SubscriptionAttribute<T extends Serializable> extends AbstractSubscription {

    private final AttributeKey<T> key;
    private final Consumer<T> callback;

    protected SubscriptionAttribute(AttributeKey<T> key, Consumer<T> callback) {
        this.key = key;
        this.callback = callback;
    }

    public void handle(AttributeKey<?> key, Object value) {
        if ( !key.equals(this.key) ) {
            return;
        }

        if ( !isNotExpired() ) {
            return;
        }

        callback.accept((T) value);
    }
}
