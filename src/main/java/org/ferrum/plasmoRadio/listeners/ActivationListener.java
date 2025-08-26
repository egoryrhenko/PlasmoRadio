package org.ferrum.plasmoRadio.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.ferrum.plasmoRadio.PlasmoRadio;
import org.ferrum.plasmoRadio.RadioAddon;
import org.ferrum.plasmoRadio.blocks.Microphone;
import org.ferrum.plasmoRadio.blocks.RadioBlock;
import org.ferrum.plasmoRadio.blocks.Speaker;
import org.ferrum.plasmoRadio.utils.RadioDeviceRegistry;
import org.jetbrains.annotations.NotNull;
import su.plo.voice.api.event.EventPriority;
import su.plo.voice.api.event.EventSubscribe;
import su.plo.voice.api.server.audio.capture.ServerActivation;
import su.plo.voice.api.server.audio.source.ServerStaticSource;
import su.plo.voice.api.server.event.audio.source.PlayerSpeakEvent;
import su.plo.voice.api.server.player.VoicePlayer;
import su.plo.voice.proto.packets.tcp.serverbound.PlayerAudioEndPacket;
import su.plo.voice.proto.packets.udp.clientbound.SourceAudioPacket;
import su.plo.voice.proto.packets.udp.serverbound.PlayerAudioPacket;

import java.util.UUID;

import static org.bukkit.Bukkit.getPlayer;




public class ActivationListener {
    public static UUID groupsUUID = UUID.fromString("46927964-3207-377a-84d7-26a82d52be1e");

    @EventSubscribe( priority = EventPriority.HIGHEST, ignoreCancelled = false )
    public void onPlayerSpeak(@NotNull PlayerSpeakEvent event) {

        if (event.getPacket().getActivationId().equals(groupsUUID)) {
            return;
        }

        VoicePlayer player = event.getPlayer();
        PlayerAudioPacket packet = event.getPacket();

        Location loc = getPlayer(player.getInstance().getUuid()).getLocation();
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
    }
}
