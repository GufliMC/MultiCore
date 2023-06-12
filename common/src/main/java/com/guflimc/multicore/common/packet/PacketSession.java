package com.guflimc.multicore.common.packet;

import com.guflimc.multicore.packet.Packet;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PacketSession<T extends Packet> extends Packet {

    private final UUID sessionId;
    private final T packet;

    public PacketSession(@NotNull UUID sessionId, @NotNull T packet) {
        this.sessionId = sessionId;
        this.packet = packet;
    }

    public UUID sessionId() {
        return sessionId;
    }

    public T packet() {
        return packet;
    }
}
