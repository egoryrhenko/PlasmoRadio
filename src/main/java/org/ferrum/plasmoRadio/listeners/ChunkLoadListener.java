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

import java.util.concurrent.atomic.AtomicInteger;

public class ChunkLoadListener implements Listener {

    public static final AtomicInteger CountLoadTask = new AtomicInteger(0);
    public static final AtomicInteger CountUnloadTask = new AtomicInteger(0);


    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(PlasmoRadio.plugin, () -> {
            CountLoadTask.incrementAndGet();
            DatabaseManager.loadBlocksInChunk(event.getChunk());
            CountLoadTask.decrementAndGet();
        });
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(PlasmoRadio.plugin, () -> {
            CountUnloadTask.incrementAndGet();
            RadioManager.unloadChunk(event.getChunk());
            CountUnloadTask.decrementAndGet();
        });
    }
}
