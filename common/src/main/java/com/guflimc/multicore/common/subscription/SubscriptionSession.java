package com.guflimc.multicore.common.subscription;

import com.guflimc.multicore.packet.Packet;
import com.guflimc.multicore.packet.RequestPacket;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class SubscriptionSession<T extends Packet, U extends RequestPacket<T>> extends AbstractSubscription {

    private final Class<U> packetType;
    private final BiConsumer<U, Consumer<T>> callback;

    protected SubscriptionSession(@NotNull Class<U> packetType, @NotNull BiConsumer<U, Consumer<T>> callback) {
        this.packetType = packetType;
        this.callback = callback;
    }

    public void handle(@NotNull Packet packet, @NotNull Consumer<T> send) {
        if ( !packetType.isInstance(packet)) {
            return;
        }

        if ( !isNotExpired() ) {
            return;
        }

        callback.accept(packetType.cast(packet), send);
    }
}
