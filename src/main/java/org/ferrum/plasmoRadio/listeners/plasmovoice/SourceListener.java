package org.ferrum.plasmoRadio.listeners.plasmovoice;

import org.ferrum.plasmoRadio.blocks.Microphone;
import org.ferrum.plasmoRadio.blocks.Speaker;
import org.ferrum.plasmoRadio.managers.RadioManager;
import org.ferrum.plasmoRadio.utils.ChunkKey;
import org.ferrum.plasmoRadio.utils.RadioDeviceRegistry;
import su.plo.slib.api.server.position.ServerPos3d;
import su.plo.voice.api.event.EventSubscribe;
import su.plo.voice.api.server.audio.source.ServerStaticSource;
import su.plo.voice.api.server.event.audio.source.ServerSourceAudioPacketEvent;
import su.plo.voice.proto.packets.udp.clientbound.SourceAudioPacket;

import java.util.Objects;
import java.util.Set;

public class SourceListener {
    @EventSubscribe
    public void onSourceAudio(ServerSourceAudioPacketEvent event) {
        if (event.getSource() instanceof ServerStaticSource staticSource) {
            if (Speaker.sourcesList.contains(staticSource)) {
                return;
            }

            SourceAudioPacket packet = event.getPacket();
            byte[] data = packet.getData();
            long sequenceNumber = packet.getSequenceNumber();
            ServerPos3d pos = staticSource.getPosition();
            String worldName = pos.getWorld().getName();
            double sx = pos.getX() - 0.5;
            double sy = pos.getY() - 0.5;
            double sz = pos.getZ() - 0.5;

            int cx = ((int) sx) >> 4;
            int cz = ((int) sz) >> 4;

            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    Set<Microphone> microphones = RadioDeviceRegistry.microphonesByChunk.get(
                            new ChunkKey(worldName, cx + dx, cz + dz)
                    );
                    if (microphones == null || microphones.isEmpty()) continue;

                    for (Microphone microphone : microphones) {
                        double dx_ = sx - microphone.location.getX();
                        double dy_ = sy - microphone.location.getY();
                        double dz_ = sz - microphone.location.getZ();
                        if (dx_ * dx_ + dy_ * dy_ + dz_ * dz_ > 4) {
                            continue;
                        }

                        String frequencyPassword = RadioManager.passwordFoFrequency.get(microphone.frequency);
                        if (frequencyPassword != null && !Objects.equals(microphone.key, frequencyPassword)) {
                            continue;
                        }

                        RadioManager.getDevices(microphone.frequency).forEach(
                                device -> device.receivePackage(staticSource, data, sequenceNumber)
                        );
                    }
                }
            }
        }
    }
}
