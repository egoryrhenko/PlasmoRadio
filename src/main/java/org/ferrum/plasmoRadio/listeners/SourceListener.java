package org.ferrum.plasmoRadio.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.ferrum.plasmoRadio.blocks.Microphone;
import org.ferrum.plasmoRadio.blocks.RadioBlock;
import org.ferrum.plasmoRadio.blocks.Speaker;
import org.ferrum.plasmoRadio.utils.RadioDeviceRegistry;
import su.plo.slib.api.server.position.ServerPos3d;
import su.plo.voice.api.event.EventSubscribe;
import su.plo.voice.api.server.audio.source.ServerStaticSource;
import su.plo.voice.api.server.event.audio.source.ServerSourceAudioPacketEvent;
import su.plo.voice.proto.packets.udp.clientbound.SourceAudioPacket;

public class SourceListener {
    @EventSubscribe
    public void onSourceAudio(ServerSourceAudioPacketEvent event) {
        if (event.getSource() instanceof ServerStaticSource staticSource) {
            if (Speaker.sourcesList.contains(staticSource)) {
                return;
            }

            SourceAudioPacket packet = event.getPacket();

            for (Microphone microphone : RadioDeviceRegistry.getMicrophones()) {
                if (getLocation(staticSource.getPosition()).distanceSquared(microphone.location) < 4) {
                    for (RadioBlock radioBlock : microphone.devices) {
                        radioBlock.test(staticSource, packet);
                    }
                }
            }
        }
    }


    public static Location getLocation(ServerPos3d loc) {
        return new Location(Bukkit.getWorld(loc.getWorld().getName()), loc.getX(), loc.getY(), loc.getZ());
    }

}
