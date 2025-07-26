package org.ferrum.plasmoRadio.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.ferrum.plasmoRadio.DatabaseManager;
import org.ferrum.plasmoRadio.PlasmoRadio;
import org.ferrum.plasmoRadio.RadioManager;

public class ChunkLoadListener implements Listener {

    public static int CountLoadTask;
    public static int CountUnloadTask;


    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(PlasmoRadio.plugin, () -> {
            CountLoadTask = CountLoadTask + 1;
            DatabaseManager.loadBlocksInChunk(event.getChunk());
            CountLoadTask = CountLoadTask - 1;
        });
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(PlasmoRadio.plugin, () -> {
            CountUnloadTask++;
            RadioManager.unloadChunk(event.getChunk());
            CountUnloadTask--;
        });
    }
}
