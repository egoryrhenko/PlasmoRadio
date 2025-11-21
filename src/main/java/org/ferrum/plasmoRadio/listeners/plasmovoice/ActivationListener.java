package org.ferrum.plasmoRadio.listeners.plasmovoice;

import org.bukkit.Location;
import org.ferrum.plasmoRadio.PlasmoRadio;
import org.ferrum.plasmoRadio.managers.RadioManager;
import org.ferrum.plasmoRadio.blocks.Microphone;
import org.ferrum.plasmoRadio.utils.RadioDeviceRegistry;
import org.jetbrains.annotations.NotNull;
import su.plo.voice.api.event.EventPriority;
import su.plo.voice.api.event.EventSubscribe;
import su.plo.voice.api.server.event.audio.source.PlayerSpeakEvent;
import su.plo.voice.api.server.player.VoicePlayer;
import su.plo.voice.proto.packets.udp.serverbound.PlayerAudioPacket;

import java.util.UUID;

import static org.bukkit.Bukkit.getPlayer;

public class ActivationListener {
    public static UUID groupsUUID = UUID.fromString("46927964-3207-377a-84d7-26a82d52be1e");

    @EventSubscribe(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerSpeak(@NotNull PlayerSpeakEvent event) {

        if (event.getPacket().getActivationId().equals(groupsUUID)) {
            return;
        }

        VoicePlayer player = event.getPlayer();
        PlayerAudioPacket packet = event.getPacket();

        Location loc = getPlayer(player.getInstance().getUuid()).getLocation().add(-0.5f, -0.5, -0.5);
        for (Microphone microphone : RadioDeviceRegistry.getMicrophones()) {
            if (!loc.getWorld().equals(microphone.location.getWorld())) {
                continue;
            }
            if (loc.distanceSquared(microphone.location) > 4) {
                continue;
            }
            RadioManager.getDevices(microphone.frequency).forEach(
                    device -> device.receivePackage(player, packet)
            );
        }
    }
}
