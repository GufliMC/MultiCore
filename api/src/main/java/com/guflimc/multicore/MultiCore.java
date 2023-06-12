package com.guflimc.multicore;

import com.guflimc.multicore.packet.Packet;
import com.guflimc.multicore.packet.RequestPacket;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface MultiCore {

    // SEND

    <T extends Packet, U extends RequestPacket<T>> CompletableFuture<T> request(@NotNull U packet);

    void send(@NotNull Packet packet);

    // RECEIVE

    <T extends Packet> Subscription subscribe(@NotNull Class<T> type, @NotNull Consumer<T> callback);

    <T extends Packet, U extends RequestPacket<T>> Subscription subscribe(@NotNull Class<U> type, @NotNull BiConsumer<U, Consumer<T>> callback);

    // DATA

    MultiStorage storage();

}
