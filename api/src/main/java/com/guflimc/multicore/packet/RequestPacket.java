package com.guflimc.multicore.packet;

public class RequestPacket<T extends Packet> extends Packet {

    private final Class<T> responseType;

    public RequestPacket(Class<T> responseType) {
        this.responseType = responseType;
    }

    public Class<T> responseType() {
        return responseType;
    }
}
