package org.ferrum.plasmoRadio.utils;

import org.bukkit.Location;
import org.ferrum.plasmoRadio.blocks.Microphone;
import org.ferrum.plasmoRadio.blocks.RadioBlock;
import org.ferrum.plasmoRadio.blocks.Speaker;

import java.util.HashMap;
import java.util.List;

public class RadioDeviceRegistry {
    public static HashMap<Location, RadioBlock> devices = new HashMap<>();

    public static void register(Location loc, RadioBlock radioBlock) {
        devices.put(loc, radioBlock);
    }

    public static void unregister(Location location) {
        devices.remove(location);
    }

    public static List<Microphone> getMicrophones() {
        return devices.values().stream()
                .filter(device -> device instanceof Microphone)
                .map(device -> (Microphone) device)
                .toList();
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