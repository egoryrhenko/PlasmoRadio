package org.ferrum.plasmoRadio.listeners.bukkit;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.ferrum.plasmoRadio.PlasmoRadio;
import org.ferrum.plasmoRadio.blocks.Locator;
import org.ferrum.plasmoRadio.blocks.Microphone;
import org.ferrum.plasmoRadio.blocks.Speaker;
import org.ferrum.plasmoRadio.managers.ChunkStorageManager;
import org.ferrum.plasmoRadio.managers.RadioManager;
import org.ferrum.plasmoRadio.utils.RadioBlockType;
import org.ferrum.plasmoRadio.utils.RadioDeviceRegistry;
import org.ferrum.plasmoRadio.utils.Scheduler;

import java.util.concurrent.atomic.AtomicInteger;

public class ChunkLoadListener implements Listener {

    public static final AtomicInteger CountLoadTask = new AtomicInteger(0);
    public static final AtomicInteger CountUnloadTask = new AtomicInteger(0);


    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Scheduler.runAtChunk(event.getChunk().getBlock(8,64,8).getLocation(), () -> {
            CountLoadTask.incrementAndGet();
            loadBlocksInChunk(event.getChunk());
            CountLoadTask.decrementAndGet();
        });
    }

    private void loadBlocksInChunk(Chunk chunk) {
        for (ChunkStorageManager.ChunkBlockData data : ChunkStorageManager.loadBlocksInChunk(chunk)) {
            Location loc = new Location(chunk.getWorld(), data.x(), data.y(), data.z());

            Material material = loc.getBlock().getType();
            if (!(material.equals(Material.PLAYER_HEAD) || material.equals(Material.PLAYER_WALL_HEAD))) {
                ChunkStorageManager.removeBlock(loc);
                continue;
            }

            try {
                RadioBlockType blockType = RadioBlockType.fromId(data.type());
                switch (blockType) {
                    case Speaker -> RadioDeviceRegistry.register(loc, new Speaker(loc, data.options()));
                    case Microphone -> RadioDeviceRegistry.register(loc, new Microphone(loc, data.options()));
                    case Locator -> RadioDeviceRegistry.register(loc, new Locator(loc, data.options()));
                }
            } catch (IllegalArgumentException e) {
                PlasmoRadio.log("Unknown radio block type at " + loc + ": " + e.getMessage());
                ChunkStorageManager.removeBlock(loc);
            }
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Scheduler.runAtChunk(event.getChunk().getBlock(8,64,8).getLocation(), () -> {
            CountUnloadTask.incrementAndGet();
            RadioManager.unloadChunk(event.getChunk());
            CountUnloadTask.decrementAndGet();
        });
    }
}
