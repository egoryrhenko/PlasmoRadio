package org.ferrum.plasmoRadio.utils;

import org.bukkit.Location;
import org.ferrum.plasmoRadio.RadioAddon;
import su.plo.slib.api.server.position.ServerPos3d;
import su.plo.slib.api.server.world.McServerWorld;
import su.plo.voice.api.server.audio.source.ServerAudioSource;
import su.plo.voice.api.server.audio.source.ServerStaticSource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class Speaker {

    public Location location;
    public ServerPos3d pos3d;
    public float frequency;

    public HashMap<UUID, ServerStaticSource> sourceByUUID = new HashMap<>();
    public static HashSet<ServerStaticSource> sourcesList = new HashSet<>();

    public Speaker(Location location) {
        this.location = location;

        McServerWorld world = RadioAddon.getVoiceServer.getMinecraftServer()
                .getWorlds()
                .stream()
                .filter(w -> w.getName().equals("world"))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("World not found"));

        this.pos3d = new ServerPos3d(world, location.x() + 0.5d,location.y() + .5d,location.z() + .5d);
        test();
    }

    public ServerStaticSource getSource(UUID uuid) {
        ServerStaticSource staticSource = sourceByUUID.get(uuid);
        if (staticSource == null) {
            staticSource = RadioAddon.sourceLine.createStaticSource(pos3d,false);
            sourceByUUID.put(uuid, staticSource);
            sourcesList.add(staticSource);
        }
        return staticSource;
    }

    public void removeSource(UUID uuid) {
        ServerStaticSource source = getSource(uuid);
        source.remove();
        sourceByUUID.remove(uuid);
        sourcesList.add(source);
    }

    public void test() {
        for (Microphone microphone : RadioManager.microphones.values()) {
            if (microphone.frequency == this.frequency) {
                microphone.speakers.add(this);
            }
        }
    }

    public void remove() {
        for (Microphone microphone : RadioManager.microphones.values()) {
            if (microphone.frequency == this.frequency) {
                microphone.speakers.remove(this);
            }
        }
        sourceByUUID.values().forEach(
                ServerAudioSource::remove
        );
        sourceByUUID.clear();
    }
}
