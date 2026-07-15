package org.ferrum.plasmoRadio.listeners.plasmovoice;

import org.ferrum.plasmoRadio.PlasmoRadio;
import org.ferrum.plasmoRadio.blocks.Microphone;
import org.ferrum.plasmoRadio.managers.RadioManager;
import org.ferrum.plasmoRadio.utils.ChunkKey;
import org.ferrum.plasmoRadio.utils.RadioDeviceRegistry;
import org.jetbrains.annotations.NotNull;
import su.plo.slib.api.server.position.ServerPos3d;
import su.plo.voice.api.event.EventPriority;
import su.plo.voice.api.event.EventSubscribe;
import su.plo.voice.api.server.event.audio.source.PlayerSpeakEvent;
import su.plo.voice.api.server.player.VoiceServerPlayer;
import su.plo.voice.proto.packets.udp.serverbound.PlayerAudioPacket;

import java.io.Console;
import java.util.Set;
import java.util.UUID;

public class ActivationListener {
    public static UUID groupsUUID = UUID.fromString("46927964-3207-377a-84d7-26a82d52be1e");

    @EventSubscribe(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerSpeak(@NotNull PlayerSpeakEvent event) {

        PlasmoRadio.log(event.getPacket().getSequenceNumber()+"");
        PlasmoRadio.log("alo");
        if (event.getPacket().getActivationId().equals(groupsUUID)) {
            return;
        }

        VoiceServerPlayer player = (VoiceServerPlayer) event.getPlayer();
        PlayerAudioPacket packet = event.getPacket();
        byte[] data = packet.getData();
        long sequenceNumber = packet.getSequenceNumber();

        ServerPos3d pos = player.getInstance().getServerPosition();
        String worldName = pos.getWorld().getName();
        double px = pos.getX();
        double py = pos.getY();
        double pz = pos.getZ();

        int cx = ((int) px) >> 4;
        int cz = ((int) pz) >> 4;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                Set<Microphone> microphones = RadioDeviceRegistry.microphonesByChunk.get(
                        new ChunkKey(worldName, cx + dx, cz + dz)
                );
                if (microphones == null || microphones.isEmpty()) continue;

                for (Microphone microphone : microphones) {
                    double dx_ = px - microphone.location.getX();
                    double dy_ = py - microphone.location.getY();
                    double dz_ = pz - microphone.location.getZ();
                    if (dx_ * dx_ + dy_ * dy_ + dz_ * dz_ > 4) {
                        continue;
                    }



                    RadioManager.getDevices(microphone.frequency).forEach(
                            device -> device.receivePackage(player, data, sequenceNumber)
                    );
                }
            }
        }
    }
}
