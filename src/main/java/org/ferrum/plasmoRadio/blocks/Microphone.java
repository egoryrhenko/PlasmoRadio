package org.ferrum.plasmoRadio.blocks;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Location;
import org.ferrum.plasmoRadio.menu.RadioBlockMenu;

import java.util.HashMap;
import java.util.Map;

public class Microphone extends RadioBlock {

    public String key;

    public Microphone(Location location) {
        this.location = location;

        frequency = 100f;
    }

    public Microphone(Location location, Map<String, String> options){
        this.location = location;
        loadOptions(options);
    }

    @Override
    public Map<String, String> getOptions() {
        Map<String, String> options = new HashMap<>(super.getOptions());
        if (key != null) {
            options.put("key", key);
        }
        return options;
    }

    @Override
    public void loadOptions(Map<String, String> options) {
        if (options.containsKey("key")) {
            this.key = options.get("key");
        }
        super.loadOptions(options);
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
