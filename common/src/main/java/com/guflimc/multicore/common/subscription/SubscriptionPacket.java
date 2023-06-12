package com.guflimc.multicore.common.subscription;

import com.guflimc.multicore.packet.Packet;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public abstract class SubscriptionPacket<T extends Packet> extends AbstractSubscription {

    private final Class<T> packetType;
    private final Consumer<T> callback;

    protected SubscriptionPacket(@NotNull Class<T> packetType, @NotNull Consumer<T> callback) {
        this.packetType = packetType;
        this.callback = callback;
    }

    public void handle(@NotNull Packet packet) {
        if ( !packetType.isInstance(packet) ) {
            return;
        }

        if ( !isNotExpired() ) {
            return;
        }

        callback.accept(packetType.cast(packet));
    }

}
