package org.ferrum.plasmoRadio.managers;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.ferrum.plasmoRadio.blocks.RadioBlock;
import org.ferrum.plasmoRadio.blocks.ReceiveRadioBlock;
import org.ferrum.plasmoRadio.utils.RadioDeviceRegistry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RadioManager {

    public static final ConcurrentHashMap<Float, HashSet<ReceiveRadioBlock>> devicesFoFrequency = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Float, String> passwordFoFrequency = new ConcurrentHashMap<>();

    public static HashSet<ReceiveRadioBlock> getDevices(Float frequency) {
        HashSet<ReceiveRadioBlock> devices = devicesFoFrequency.get(frequency);
        return devices != null ? devices : new HashSet<>();
    }

    public static void unloadChunk(Chunk chunk) {
        String worldName = chunk.getWorld().getName();
        int minX = chunk.getX() << 4;
        int maxX = minX + 15;
        int minZ = chunk.getZ() << 4;
        int maxZ = minZ + 15;

        List<Location> toRemove = new ArrayList<>();
        for (Map.Entry<Location, RadioBlock> entry : RadioDeviceRegistry.devices.entrySet()) {
            Location loc = entry.getKey();

            if (!loc.getWorld().getName().equals(worldName)) continue;

            int x = loc.getBlockX();
            int z = loc.getBlockZ();

            if (x >= minX && x <= maxX && z >= minZ && z <= maxZ) {
                entry.getValue().remove();
                toRemove.add(loc);
            }
        }

        for (Location loc : toRemove) {
            RadioDeviceRegistry.unregister(loc);
        }
    }
}
