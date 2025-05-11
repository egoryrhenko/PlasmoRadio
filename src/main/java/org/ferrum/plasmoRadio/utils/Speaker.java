package org.ferrum.plasmoRadio.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Skull;
import org.ferrum.plasmoRadio.RadioAddon;
import su.plo.slib.api.server.position.ServerPos3d;
import su.plo.slib.api.server.world.McServerWorld;
import su.plo.voice.api.server.PlasmoVoiceServer;
import su.plo.voice.api.server.audio.line.ServerSourceLine;
import su.plo.voice.api.server.audio.source.ServerStaticSource;
import su.plo.voice.api.server.player.VoicePlayer;

import java.util.HashMap;
import java.util.UUID;

public class Speaker {

    public Location location;
    public ServerPos3d pos3d;
    public float frequency;

    HashMap<VoicePlayer, ServerStaticSource> sourceByPlayer = new HashMap<>();

    public Speaker(Location location) {
        this.location = location;

        McServerWorld world = RadioAddon.getVoiceServer.getMinecraftServer()
                .getWorlds()
                .stream()
                .filter(w -> w.getName().equals("world"))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("World not found"));

        this.pos3d = new ServerPos3d(world, location.x(),location.y(),location.z());
        test();
    }

    public ServerStaticSource getSource(VoicePlayer player) {
        ServerStaticSource staticSource = sourceByPlayer.get(player);
        if (staticSource == null) {
            staticSource = RadioAddon.sourceLine.createStaticSource(pos3d,false);
            sourceByPlayer.put(player, staticSource);
        }
        return staticSource;
    }

    public void removeSource(VoicePlayer player) {
        getSource(player).remove();
        sourceByPlayer.remove(player);
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
        sourceByPlayer.values().forEach(
                (s) -> s.remove()
        );
        sourceByPlayer.clear();
    }
}
