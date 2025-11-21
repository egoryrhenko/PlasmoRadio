package org.ferrum.plasmoRadio.blocks;

import org.ferrum.plasmoRadio.PlasmoRadio;
import org.ferrum.plasmoRadio.managers.DatabaseManager;
import org.ferrum.plasmoRadio.managers.RadioManager;
import su.plo.voice.api.server.audio.source.ServerStaticSource;
import su.plo.voice.api.server.player.VoicePlayer;
import su.plo.voice.proto.packets.udp.clientbound.SourceAudioPacket;
import su.plo.voice.proto.packets.udp.serverbound.PlayerAudioPacket;

import java.util.HashSet;
import java.util.Objects;

public abstract class ReceiveRadioBlock extends RadioBlock {

    public void changeFrequency(Float newFrequency) {
        newFrequency = validFrequency(newFrequency);

        if (Objects.equals(this.frequency, newFrequency)) {
            return;
        }

        HashSet<ReceiveRadioBlock> set = RadioManager.devicesFoFrequency.get(frequency);
        if (set != null) {
            set.remove(this);
            if (set.isEmpty()) {
                RadioManager.devicesFoFrequency.remove(frequency);
            }
        }

        this.frequency = newFrequency;

        RadioManager.devicesFoFrequency
                .computeIfAbsent(newFrequency, f -> new HashSet<>())
                .add(this);
        saveOptions();
    }

    public void setFrequency(Float newFrequency) {
        newFrequency = validFrequency(newFrequency);

        this.frequency = newFrequency;

        RadioManager.devicesFoFrequency
                .computeIfAbsent(frequency, f -> new HashSet<>())
                .add(this);
        saveOptions();
    }

    public abstract void receivePackage(ServerStaticSource staticSource, SourceAudioPacket packet);
    public abstract void receivePackage(VoicePlayer player, PlayerAudioPacket packet);
}
