package com.guflimc.multicore.multipaper.packet;

import com.guflimc.multicore.packet.Packet;

import java.util.UUID;

public class PacketPlayerJoin extends Packet {

    private final UUID playerId;

    public PacketPlayerJoin(UUID playerId) {
        this.playerId = playerId;
    }

    public UUID playerId() {
        return playerId;
    }
}
