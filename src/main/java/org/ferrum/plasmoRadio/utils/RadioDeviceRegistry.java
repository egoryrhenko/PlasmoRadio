package org.ferrum.plasmoRadio.utils;

import org.bukkit.Location;
import org.ferrum.plasmoRadio.blocks.Microphone;
import org.ferrum.plasmoRadio.blocks.RadioBlock;
import org.ferrum.plasmoRadio.blocks.Speaker;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RadioDeviceRegistry {
    public static final ConcurrentHashMap<Location, RadioBlock> devices = new ConcurrentHashMap<>();

    // Index of microphones by chunk for fast lookup in voice events.
    public static final ConcurrentHashMap<ChunkKey, Set<Microphone>> microphonesByChunk = new ConcurrentHashMap<>();

    public static void register(Location loc, RadioBlock radioBlock) {
        devices.put(loc, radioBlock);
        if (radioBlock instanceof Microphone microphone) {
            microphonesByChunk
                    .computeIfAbsent(ChunkKey.from(loc), k -> ConcurrentHashMap.newKeySet())
                    .add(microphone);
        }
    }

    public static void unregister(Location location) {
        RadioBlock removed = devices.remove(location);
        if (removed instanceof Microphone microphone) {
            Set<Microphone> set = microphonesByChunk.get(ChunkKey.from(location));
            if (set != null) {
                set.remove(microphone);
                if (set.isEmpty()) {
                    microphonesByChunk.remove(ChunkKey.from(location));
                }
            }
        }
    }

    public static List<Speaker> getSpeakers() {
        return devices.values().stream()
                .filter(device -> device instanceof Speaker)
                .map(device -> (Speaker) device)
                .toList();
    }

    public static RadioBlock get(Location loc) {
        return devices.get(loc);
    }
}
