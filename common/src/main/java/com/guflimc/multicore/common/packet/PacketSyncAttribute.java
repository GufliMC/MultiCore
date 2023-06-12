package com.guflimc.multicore.common.packet;

import com.guflimc.multicore.attribute.AttributeKey;
import com.guflimc.multicore.packet.Packet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.UUID;

public class PacketSyncAttribute<T extends Serializable> extends Packet {

    private final UUID playerId;
    private final AttributeKey<T> key;
    private final T value;

    public PacketSyncAttribute(@Nullable UUID playerId, @NotNull AttributeKey<T> key, @Nullable T value) {
        this.playerId = playerId;
        this.key = key;
        this.value = value;
    }

    public UUID playerId() {
        return playerId;
    }

    public AttributeKey<T> key() {
        return key;
    }

    public T value() {
        return value;
    }
}
