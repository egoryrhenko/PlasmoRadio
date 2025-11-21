package org.ferrum.plasmoRadio.blocks;

import org.bukkit.Location;
import org.ferrum.plasmoRadio.managers.DatabaseManager;

import java.util.Map;

public class Microphone extends RadioBlock {

    public Microphone(Location location) {
        this.location = location;
        frequency = 100f;
    }

    public Microphone(Location location, Map<String, String> options){
        this.location = location;
        loadOptions(options);
    }

    @Override
    public void changeFrequency(Float newFrequency) {
        newFrequency = validFrequency(newFrequency);
        frequency = newFrequency;
        saveOptions();
    }

    @Override
    public void remove() {

    }
}
