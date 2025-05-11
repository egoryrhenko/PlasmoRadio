package org.ferrum.plasmoRadio;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.ferrum.plasmoRadio.utils.Microphone;
import org.ferrum.plasmoRadio.utils.RadioAudioForwarder;
import org.ferrum.plasmoRadio.utils.RadioManager;
import org.ferrum.plasmoRadio.utils.Speaker;
import su.plo.voice.api.addon.AddonInitializer;
import su.plo.voice.api.addon.InjectPlasmoVoice;
import su.plo.voice.api.addon.annotation.Addon;
import su.plo.voice.api.server.PlasmoVoiceServer;
import su.plo.voice.api.server.audio.capture.ProximityServerActivationHelper;
import su.plo.voice.api.server.audio.capture.ServerActivation;
import su.plo.voice.api.server.audio.line.ServerSourceLine;
import su.plo.voice.api.server.audio.source.ServerBroadcastSource;
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
    public PlasmoVoiceServer voiceServer;
    public static PlasmoVoiceServer getVoiceServer;



    public static ServerSourceLine sourceLine;

    @Override
    public void onAddonInitialize() {
        getVoiceServer = voiceServer;
        ServerActivation activation = voiceServer.getActivationManager()
                .getActivationByName("proximity")
                .orElseThrow(() -> new IllegalStateException("Proximity activation not found"));

        sourceLine = voiceServer.getSourceLineManager().createBuilder(
                this,
                "radio", // name
                "pv.activation.radio", // translation key
                "plasmovoice:textures/icons/speaker_priority.png", // icon resource location
                10 // weight
        ).build();

        //this.proximityHelper = new ProximityServerActivationHelper(voiceServer, activation, sourceLine);
        //proximityHelper.registerListeners(this);

        //new RadioAudioForwarder(activation);

        activation.onPlayerActivation((player, packet) -> {
            PlasmoRadio.log("Ишу");
            Location loc = Bukkit.getPlayer(player.getInstance().getUuid()).getLocation();
            PlasmoRadio.log(RadioManager.microphones.values().size()+"");
            for (Microphone microphone : RadioManager.microphones.values()) {
                if (loc.distanceSquared(microphone.location) < 4) {
                    PlasmoRadio.log(microphone.frequency + " = 0");
                    for (Speaker speaker : microphone.speakers) {
                        PlasmoRadio.log("бубубу "+speaker.getSource(player).getId());
                        speaker.getSource(player).sendAudioPacket(
                                new SourceAudioPacket(
                                        packet.getSequenceNumber(),
                                        (byte) speaker.getSource(player).getState(),
                                        packet.getData(),
                                        speaker.getSource(player).getId(),
                                        (short) 0
                                ),
                                (short) 10
                        );
                    }
                }

            }
            return ServerActivation.Result.IGNORED;
        });

        activation.onPlayerActivationEnd((player, packet) -> {
            for (Speaker speaker : RadioManager.speakers.values()) {
                speaker.removeSource(player);
            }
            return ServerActivation.Result.IGNORED;
        });

        System.out.println("Addon initialized");
    }

    @Override
    public void onAddonShutdown() {

    }
}