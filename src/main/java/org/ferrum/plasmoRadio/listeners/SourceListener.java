package org.ferrum.plasmoRadio.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.ferrum.plasmoRadio.PlasmoRadio;
import org.ferrum.plasmoRadio.utils.Microphone;
import org.ferrum.plasmoRadio.utils.RadioManager;
import org.ferrum.plasmoRadio.utils.Speaker;
import su.plo.slib.api.server.position.ServerPos3d;
import su.plo.voice.api.event.EventSubscribe;
import su.plo.voice.api.server.audio.source.ServerStaticSource;
import su.plo.voice.api.server.event.audio.source.ServerSourceAudioPacketEvent;
import su.plo.voice.proto.packets.udp.clientbound.SourceAudioPacket;

public class SourceListener {
    @EventSubscribe
    public void onSourceAudio(ServerSourceAudioPacketEvent event) {
        if (event.getSource() instanceof ServerStaticSource staticSource) {
            PlasmoRadio.log(staticSource.getId().equals(event.getPacket().getSourceId())+"");
            if (Speaker.sourcesList.contains(staticSource)) {
                return;
            }

            Location loc = getLocation(staticSource.getPosition());
            SourceAudioPacket packet = event.getPacket();

            for (Microphone microphone : RadioManager.microphones.values()) {
                if (loc.distanceSquared(microphone.location) < 4) {
                    for (Speaker speaker : microphone.speakers) {
                        ServerStaticSource source = speaker.getSource(staticSource.getId());
                        source.sendAudioPacket(
                                new SourceAudioPacket(
                                        packet.getSequenceNumber(),
                                        (byte) source.getState(),
                                        packet.getData(),
                                        source.getId(),
                                        (short) 10
                                ),
                                (short) 10
                        );
                    }
                }
            }
        }
    }


    public static Location getLocation(ServerPos3d loc) {
        return new Location(Bukkit.getWorld(loc.getWorld().getName()), loc.getX(), loc.getY(), loc.getZ());
    }

}
