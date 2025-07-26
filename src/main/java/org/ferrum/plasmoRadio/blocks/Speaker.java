package org.ferrum.plasmoRadio.blocks;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.ferrum.plasmoRadio.PlasmoRadio;
import org.ferrum.plasmoRadio.RadioAddon;
import org.ferrum.plasmoRadio.utils.RadioDeviceRegistry;
import su.plo.slib.api.server.position.ServerPos3d;
import su.plo.slib.api.server.world.McServerWorld;
import su.plo.voice.api.server.audio.source.ServerAudioSource;
import su.plo.voice.api.server.audio.source.ServerStaticSource;
import su.plo.voice.api.server.player.VoicePlayer;
import su.plo.voice.proto.packets.udp.clientbound.SourceAudioPacket;
import su.plo.voice.proto.packets.udp.serverbound.PlayerAudioPacket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class Speaker extends RadioBlock {

    public ServerPos3d pos3d;

    public HashMap<UUID, ServerStaticSource> sourceByUUID = new HashMap<>();
    public static HashSet<ServerStaticSource> sourcesList = new HashSet<>();

    public Speaker(Location location) {
        this.location = location;

        McServerWorld world = RadioAddon.staticVoiceServer.getMinecraftServer()
                .getWorlds()
                .stream()
                .filter(w -> w.getName().equals(location.getWorld().getName()))
                .findAny()
                .orElseThrow(() -> new IllegalStateException(location.getWorld().getName() + " not found"));

        this.pos3d = new ServerPos3d(world, location.x() + 0.5d,location.y() + .5d,location.z() + .5d);
        update();
    }

    public ServerStaticSource getSource(UUID uuid) {
        ServerStaticSource staticSource = sourceByUUID.get(uuid);
        if (staticSource == null) {
            staticSource = RadioAddon.sourceLine.createStaticSource(pos3d,false);
            staticSource.setIconVisible(false);
            sourceByUUID.put(uuid, staticSource);
            sourcesList.add(staticSource);
        }
        return staticSource;
    }

    public void removeSource(UUID uuid) {
        ServerStaticSource source = getSource(uuid);
        source.remove();
        sourceByUUID.remove(uuid);
        sourcesList.add(source);
    }

    @Override
    public void update() {
        for (Microphone microphone : RadioDeviceRegistry.getMicrophones()) {
            if (microphone.devices.contains(this) && microphone.frequency != this.frequency) {
                microphone.devices.remove(this);
                PlasmoRadio.log("remove s");
            }
            if (!microphone.devices.contains(this) && microphone.frequency == this.frequency) {
                microphone.devices.add(this);
                PlasmoRadio.log("add s");
            }
        }
    }

    @Override
    public void test(VoicePlayer player, PlayerAudioPacket packet) {
        ServerStaticSource source = getSource(player.getInstance().getUuid());
        source.sendAudioPacket(
                new SourceAudioPacket(
                        packet.getSequenceNumber(),
                        (byte) source.getState(),
                        packet.getData(),
                        source.getId(),
                        (short) 16
                ),
                (short) 16
        );
    }

    @Override
    public void test(ServerStaticSource staticSource, SourceAudioPacket packet) {
        ServerStaticSource source = getSource(staticSource.getId());
        source.sendAudioPacket(
                new SourceAudioPacket(
                        packet.getSequenceNumber(),
                        (byte) source.getState(),
                        packet.getData(),
                        source.getId(),
                        (short) 16
                ),
                (short) 16
        );
    }
    @Override
    public void remove() {
        for (Microphone microphone : RadioDeviceRegistry.getMicrophones()) {
            if (microphone.devices.contains(this)) {
                microphone.devices.remove(this);
                PlasmoRadio.log("remove fo delete");
            }
        }
        sourceByUUID.values().forEach(
                ServerAudioSource::remove
        );
        sourceByUUID.clear();
    }

}
