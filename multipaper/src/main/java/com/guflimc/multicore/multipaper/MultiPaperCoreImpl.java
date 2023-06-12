package com.guflimc.multicore.multipaper;

import com.github.puregero.multilib.MultiLib;
import com.guflimc.multicore.MultiStorage;
import com.guflimc.multicore.common.AbstractMultiCore;
import com.guflimc.multicore.common.adapter.SerializableAdapter;
import com.guflimc.multicore.common.packet.PacketSyncAttribute;
import com.guflimc.multicore.multipaper.packet.PacketPlayerQuit;
import com.guflimc.multicore.packet.Packet;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;
import java.util.Map;
import java.util.UUID;

final class MultiPaperCoreImpl extends AbstractMultiCore implements MultiPaperCore {

    private final String key;

    public MultiPaperCoreImpl(@NotNull JavaPlugin plugin) {
        key = "MultiCore:" + plugin.getName();

        // subscribe to this plugin's channel
        MultiLib.on(plugin, key, this::recieve);

        // load all caches
        Map<String, String> data = MultiLib.getDataStorage().list(key).join();
        for (String key : data.keySet()) {
            byte[] decoded = Base64.getDecoder().decode(data.get(key));
            AttributePair<?> pair = (AttributePair<?>) SerializableAdapter.adapt(decoded);

            String prefix = key.substring(0, key.indexOf(";"));
            if (prefix.equals(this.key)) {
                storage().update(pair.key(), pair.value());
                continue;
            }

            UUID playerId = UUID.fromString(prefix.substring(key.length() + 1));
            storage(playerId).update(pair.key(), pair.value());
        }

        // player quit
        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                send(new PacketPlayerQuit(event.getPlayer().getUniqueId()));
                invalidate(event.getPlayer().getUniqueId());
            }
        }, plugin);

        subscribe(PacketPlayerQuit.class, packet -> {
            invalidate(packet.playerId());
        });
    }

    @Override
    public void send(@NotNull Packet packet) {
        super.send(packet);

        if (packet instanceof PacketSyncAttribute<?> psa) {
            String encoded = Base64.getEncoder().encodeToString(SerializableAdapter.adapt(new AttributePair<>(psa.key(), psa.value())));
            if (psa.playerId() == null) {
                MultiLib.getDataStorage().set(key + ";" + psa.key().id(), encoded);
            } else {
                MultiLib.getDataStorage().set(key + ":" + psa.playerId().toString() + ";" + psa.key().id(), encoded);
            }
        }
    }

    @Override
    protected void send(byte @NotNull [] packet) {
        MultiLib.notify(key, packet);
    }

    public MultiStorage storage(@NotNull Player player) {
        return super.storage(player.getUniqueId());
    }
}
