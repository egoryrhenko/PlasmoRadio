package org.ferrum.plasmoRadio.utils;

import org.bukkit.Location;

import java.util.HashSet;

public class Microphone {

    public Location location;
    public float frequency;

    public Microphone(Location location) {
        this.location = location;
        test();
    }

    public HashSet<Speaker> speakers = new HashSet<>();

    public void test() {
        for (Speaker speaker : RadioManager.speakers.values()) {
            if (speaker.frequency == frequency) {
                speakers.add(speaker);
            }
        }
    }

    public void remove() {
        speakers.clear();
    }

}
