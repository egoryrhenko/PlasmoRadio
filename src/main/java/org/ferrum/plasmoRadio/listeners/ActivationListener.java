package org.ferrum.plasmoRadio.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.ferrum.plasmoRadio.blocks.Microphone;
import org.ferrum.plasmoRadio.blocks.RadioBlock;
import org.ferrum.plasmoRadio.blocks.Speaker;
import org.ferrum.plasmoRadio.utils.RadioDeviceRegistry;
import su.plo.voice.api.server.audio.capture.ServerActivation;
import su.plo.voice.api.server.audio.source.ServerStaticSource;
import su.plo.voice.api.server.player.VoicePlayer;
import su.plo.voice.proto.packets.tcp.serverbound.PlayerAudioEndPacket;
import su.plo.voice.proto.packets.udp.clientbound.SourceAudioPacket;
import su.plo.voice.proto.packets.udp.serverbound.PlayerAudioPacket;

public class ActivationListener {

    private final ServerActivation activation;

    public ActivationListener(ServerActivation activation) {
        this.activation = activation;

        this.activation.onPlayerActivation(this::onActivation);
        this.activation.onPlayerActivationEnd(this::onActivationEnd);
    }

    private ServerActivation.Result onActivation(VoicePlayer player, PlayerAudioPacket packet) {
        Location loc = Bukkit.getPlayer(player.getInstance().getUuid()).getLocation();
        for (Microphone microphone : RadioDeviceRegistry.getMicrophones()) {
            if (!loc.getWorld().equals(microphone.location.getWorld())) {
                continue;
            }
            if (loc.distanceSquared(microphone.location) < 4) {
                for (RadioBlock radioBlock : microphone.devices) {
                    radioBlock.test(player, packet);
                }
            }

        }
        return ServerActivation.Result.IGNORED;
    }

    public ServerActivation.Result onActivationEnd(VoicePlayer player, PlayerAudioEndPacket packet) {
        for (Speaker speaker : RadioDeviceRegistry.getSpeakers()) {
            speaker.removeSource(player.getInstance().getUuid());
        }
        return ServerActivation.Result.IGNORED;
    }
}
