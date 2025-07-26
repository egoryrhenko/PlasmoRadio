package org.ferrum.plasmoRadio.blocks;

import org.bukkit.Location;
import su.plo.voice.api.server.audio.source.ServerStaticSource;
import su.plo.voice.api.server.player.VoicePlayer;
import su.plo.voice.proto.packets.udp.clientbound.SourceAudioPacket;
import su.plo.voice.proto.packets.udp.serverbound.PlayerAudioPacket;

public abstract class RadioBlock {
    public Location location;
    public float frequency;

    public abstract void update();
    public abstract void remove();
    public abstract void test(ServerStaticSource staticSource, SourceAudioPacket packet);
    public abstract void test(VoicePlayer player, PlayerAudioPacket packet);

}
