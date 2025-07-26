package org.ferrum.plasmoRadio;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.ferrum.plasmoRadio.listeners.ActivationListener;
import org.ferrum.plasmoRadio.listeners.SourceListener;
import org.ferrum.plasmoRadio.blocks.Microphone;
import org.ferrum.plasmoRadio.blocks.Speaker;
import su.plo.voice.api.addon.AddonInitializer;
import su.plo.voice.api.addon.InjectPlasmoVoice;
import su.plo.voice.api.addon.annotation.Addon;
import su.plo.voice.api.server.PlasmoVoiceServer;
import su.plo.voice.api.server.audio.capture.ServerActivation;
import su.plo.voice.api.server.audio.line.ServerSourceLine;
import su.plo.voice.api.server.audio.source.ServerStaticSource;
import su.plo.voice.proto.packets.udp.clientbound.SourceAudioPacket;

@Addon(
        // An addon id must start with a lowercase letter and may contain only lowercase letters, digits, hyphens, and underscores.
        // It should be between 4 and 32 characters long.
        id = "pv-addon-radio",
        name = "Radio",
        version = "1.0.0",
        authors = {"Egor"}
)
public final class RadioAddon implements AddonInitializer {

    @InjectPlasmoVoice
    private PlasmoVoiceServer voiceServer;

    public static PlasmoVoiceServer staticVoiceServer;

    //private static ServerActivation activation;

    //private static ServerActivation.PlayerActivationListener activationListener = RadioManager.onActivation();
    //private static ServerActivation.PlayerActivationEndListener endListener = RadioManager.onActivationEnd();;

    public static ServerSourceLine sourceLine;

    @Override
    public void onAddonInitialize() {
        staticVoiceServer = voiceServer;

        //RadioManager.init();

        ServerActivation activation = voiceServer.getActivationManager()
                .getActivationByName("proximity")
                .orElseThrow(() -> new IllegalStateException("Proximity activation not found"));

        PlasmoRadio.log(activation.toString());

        sourceLine = voiceServer.getSourceLineManager().createBuilder(
                this,
                "radio", // name
                "pv.activation.radio", // translation key
                "plasmovoice:textures/icons/speaker_priority.png", // icon resource location
                10 // weight
        ).build();

//        activation.onPlayerActivation((player, packet) -> {
//            Location loc = Bukkit.getPlayer(player.getInstance().getUuid()).getLocation();
//            for (Microphone microphone : RadioManager.microphones.values()) {
//                if (!loc.getWorld().equals(microphone.location.getWorld())) {
//                    continue;
//                }
//                if (loc.distanceSquared(microphone.location) < 4) {
//                    for (Speaker speaker : microphone.speakers) {
//                        ServerStaticSource source = speaker.getSource(player.getInstance().getUuid());
//                        source.sendAudioPacket(
//                                new SourceAudioPacket(
//                                        packet.getSequenceNumber(),
//                                        (byte) source.getState(),
//                                        packet.getData(),
//                                        source.getId(),
//                                        (short) 16
//                                ),
//                                (short) 16
//                        );
//                    }
//                }
//
//            }
//            return ServerActivation.Result.IGNORED;
//        });
//
//        activation.onPlayerActivationEnd((player, packet) -> {
//            for (Speaker speaker : RadioManager.speakers.values()) {
//                speaker.removeSource(player.getInstance().getUuid());
//            }
//            return ServerActivation.Result.IGNORED;
//        });

        voiceServer.getEventBus().register(this, new ActivationListener(activation));

        voiceServer.getEventBus().register(this, new SourceListener());

        PlasmoRadio.log("[===================]");
        PlasmoRadio.log("[ Addon initialized ]");
        PlasmoRadio.log("[===================]");
    }

    @Override
    public void onAddonShutdown() {
        //activation.removePlayerActivationListener(activationListener);
        //activation.removePlayerActivationEndListener(endListener);
    }

    public ServerActivation getActivation() {
        return voiceServer.getActivationManager()
                .getActivationByName("proximity")
                .orElseThrow(() -> new IllegalStateException("Proximity activation not found"));
    }

    public ServerSourceLine getSourceLine() {
        return voiceServer.getSourceLineManager().createBuilder(
                this,
                "radio", // name
                "pv.activation.radio", // translation key
                "plasmovoice:textures/icons/speaker_priority.png", // icon resource location
                10 // weight
        ).build();
    }
}