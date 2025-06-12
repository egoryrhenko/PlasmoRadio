package org.ferrum.plasmoRadio;

import org.ferrum.plasmoRadio.listeners.SourceListener;
import org.ferrum.plasmoRadio.utils.RadioManager;
import su.plo.voice.api.addon.AddonInitializer;
import su.plo.voice.api.addon.InjectPlasmoVoice;
import su.plo.voice.api.addon.annotation.Addon;
import su.plo.voice.api.server.PlasmoVoiceServer;
import su.plo.voice.api.server.audio.capture.ServerActivation;
import su.plo.voice.api.server.audio.line.ServerSourceLine;

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

    private static ServerActivation activation;

    private static ServerActivation.PlayerActivationListener activationListener = RadioManager.onActivation();
    private static ServerActivation.PlayerActivationEndListener endListener = RadioManager.onActivationEnd();;

    public static ServerSourceLine sourceLine;

    @Override
    public void onAddonInitialize() {
        getVoiceServer = voiceServer;

        RadioManager.init();

        activation = getActivation();

        sourceLine = getSourceLine();

        activation.onPlayerActivation(activationListener);
        activation.onPlayerActivationEnd(endListener);

        voiceServer.getEventBus().register(this, new SourceListener());

        PlasmoRadio.log("[===================]");
        PlasmoRadio.log("[ Addon initialized ]");
        PlasmoRadio.log("[===================]");
    }

    @Override
    public void onAddonShutdown() {
        activation.removePlayerActivationListener(activationListener);
        activation.removePlayerActivationEndListener(endListener);
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