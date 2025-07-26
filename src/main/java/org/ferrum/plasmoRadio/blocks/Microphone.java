package org.ferrum.plasmoRadio.blocks;

import org.bukkit.Location;
import org.ferrum.plasmoRadio.utils.RadioDeviceRegistry;
import su.plo.voice.api.server.audio.source.ServerStaticSource;
import su.plo.voice.api.server.player.VoicePlayer;
import su.plo.voice.proto.packets.udp.clientbound.SourceAudioPacket;
import su.plo.voice.proto.packets.udp.serverbound.PlayerAudioPacket;

import java.util.HashSet;

public class Microphone extends RadioBlock {

    public Microphone(Location location) {
        this.location = location;
        update();
    }

    public HashSet<RadioBlock> devices = new HashSet<>();

    @Override
    public void update() {
        devices.clear();
        for (RadioBlock radioBlock: RadioDeviceRegistry.devices.values()) {
            if (radioBlock instanceof Microphone) {
                continue;
            }
            if (radioBlock.frequency == frequency) {
                devices.add(radioBlock);
            }
        }
    }

    @Override
    public void test(VoicePlayer player, PlayerAudioPacket packet) {

    }
    @Override
    public void test(ServerStaticSource staticSource, SourceAudioPacket packet) {

    }
    @Override
    public void remove() {
        devices.clear();
    }

}
