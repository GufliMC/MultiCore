package com.guflimc.multicore.common;

import com.guflimc.multicore.MultiCore;
import com.guflimc.multicore.Subscription;
import com.guflimc.multicore.attribute.AttributeKey;
import com.guflimc.multicore.common.adapter.SerializableAdapter;
import com.guflimc.multicore.common.packet.PacketSession;
import com.guflimc.multicore.common.packet.PacketSyncAttribute;
import com.guflimc.multicore.common.subscription.SubscriptionPacket;
import com.guflimc.multicore.common.subscription.SubscriptionSession;
import com.guflimc.multicore.packet.Packet;
import com.guflimc.multicore.packet.RequestPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class AbstractMultiCore implements MultiCore {

    private final Logger LOGGER = LoggerFactory.getLogger(AbstractMultiCore.class);

    //

    private final Map<UUID, Consumer<Packet>> callbacks = new ConcurrentHashMap<>();

    private final Collection<SubscriptionPacket<?>> packetSubscriptions = new CopyOnWriteArraySet<>();
    private final Collection<SubscriptionSession<?, ?>> sessionSubscriptions = new CopyOnWriteArraySet<>();

    private final AbstractMultiStorage storage;
    private final Map<UUID, AbstractMultiStorage> players = new ConcurrentHashMap<>();

    public AbstractMultiCore() {
        this.storage = new AbstractMultiStorage() {
            @Override
            protected <T extends Serializable> void sync(@NotNull AttributeKey<T> key, @Nullable T value) {
                send(new PacketSyncAttribute<>(null, key, value));
            }
        };

        // update storage values
        subscribe(PacketSyncAttribute.class, packet -> {
            if (packet.playerId() == null) {
                LOGGER.debug("Received server attribute sync with attribute {}", packet.key().id());
                storage.update(packet.key(), packet.value());
            } else {
                LOGGER.debug("Received attribute sync for player {} with attribute {}", packet.playerId(), packet.key().id());
                storage(packet.playerId()).update(packet.key(), packet.value());
            }
        });
    }

    // SEND

    @Override
    public <T extends Packet, U extends RequestPacket<T>> CompletableFuture<T> request(@NotNull U packet) {
        CompletableFuture<T> future = new CompletableFuture<>();

        UUID sessionId = UUID.randomUUID();
        callbacks.put(sessionId, response -> future.complete((T) response));
        send(new PacketSession<>(sessionId, packet));

        return future;
    }

    @Override
    public void send(@NotNull Packet packet) {
        LOGGER.debug("Sending packet: {}", packet.getClass().getName());
        send(SerializableAdapter.adapt(packet));
    }

    protected abstract void send(byte @NotNull [] packet);

    // RECEIVE

    @Override
    public <T extends Packet> Subscription subscribe(@NotNull Class<T> type, @NotNull Consumer<T> callback) {
        SubscriptionPacket<T> sub = new SubscriptionPacket<>(type, callback) {
            @Override
            public void unsubscribe() {
                packetSubscriptions.remove(this);
            }
        };
        packetSubscriptions.add(sub);
        return sub;
    }

    @Override
    public <T extends Packet, U extends RequestPacket<T>> Subscription subscribe(@NotNull Class<U> type, @NotNull BiConsumer<U, Consumer<T>> callback) {
        SubscriptionSession<T, U> sub = new SubscriptionSession<>(type, callback) {
            @Override
            public void unsubscribe() {
                sessionSubscriptions.remove(this);
            }
        };
        sessionSubscriptions.add(sub);
        return sub;
    }

    // DATA

    @Override
    public AbstractMultiStorage storage() {
        return storage;
    }

    protected AbstractMultiStorage storage(@NotNull UUID playerId) {
        if (players.containsKey(playerId)) {
            return players.get(playerId);
        }

        LOGGER.debug("Creating storage for player: {}", playerId);
        AbstractMultiStorage storage = new AbstractMultiStorage() {
            @Override
            protected <T extends Serializable> void sync(@NotNull AttributeKey<T> key, @Nullable T value) {
                send(new PacketSyncAttribute<>(playerId, key, value));
            }
        };

        players.put(playerId, storage);
        return storage;
    }

    protected void invalidate(@NotNull UUID playerId) {
        LOGGER.debug("Invalidating storage for player: {}", playerId);
        players.remove(playerId);
    }

    //

    protected void recieve(byte @NotNull [] message) {
        Packet packet = (Packet) SerializableAdapter.adapt(message);
        LOGGER.debug("Received packet: {}", packet.getClass().getName());

        if (packet instanceof PacketSession<?> sp) {
            if (callbacks.containsKey(sp.sessionId())) {
                callbacks.remove(sp.sessionId()).accept(sp.packet());
                return;
            }

            // packet request subscriptions
            sessionSubscriptions.forEach(sub ->
                    sub.handle(sp.packet(), response -> send(new PacketSession<>(sp.sessionId(), response))));
            return;
        }

        // packet subscriptions
        packetSubscriptions.forEach(sub -> sub.handle(packet));
    }
}
