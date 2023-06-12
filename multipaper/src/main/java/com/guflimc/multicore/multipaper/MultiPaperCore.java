package com.guflimc.multicore.multipaper;

import com.guflimc.multicore.MultiCore;
import com.guflimc.multicore.MultiStorage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public interface MultiPaperCore extends MultiCore {

    static MultiPaperCore of(@NotNull JavaPlugin plugin) {
        return new MultiPaperCoreImpl(plugin);
    }

    //

    MultiStorage storage(@NotNull Player player);

}
