package com.guflimc.multicore.multipaper.packet;

import com.guflimc.multicore.packet.Packet;

import java.util.UUID;

public class PacketPlayerQuit extends Packet {

    private final UUID playerId;

    public PacketPlayerQuit(UUID playerId) {
        this.playerId = playerId;
    }

    public UUID playerId() {
        return playerId;
    }
}
