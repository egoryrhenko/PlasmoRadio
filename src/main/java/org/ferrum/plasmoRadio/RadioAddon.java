package org.ferrum.plasmoRadio;

import org.ferrum.plasmoRadio.listeners.ActivationListener;
import org.ferrum.plasmoRadio.listeners.SourceListener;
import su.plo.voice.api.addon.AddonInitializer;
import su.plo.voice.api.addon.InjectPlasmoVoice;
import su.plo.voice.api.addon.annotation.Addon;
import su.plo.voice.api.server.PlasmoVoiceServer;
import su.plo.voice.api.server.audio.line.ServerSourceLine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Addon(
        // An addon id must start with a lowercase letter and may contain only lowercase letters, digits, hyphens, and underscores.
        // It should be between 4 and 32 characters long.
        id = "pv-addon-radio",
        name = "Radio",
        version = "1.0.0",
        authors = {"Egor_", "ChatGPT"}
)
public final class RadioAddon implements AddonInitializer {

    @InjectPlasmoVoice
    private PlasmoVoiceServer voiceServer;

    public static PlasmoVoiceServer staticVoiceServer;

    public static ServerSourceLine sourceLine;

    @Override
    public void onAddonInitialize() {
        staticVoiceServer = voiceServer;

        sourceLine = voiceServer.getSourceLineManager().createBuilder(
                this,
                "radio", // name
                "pv.activation.radio", // translation key
                getClass().getClassLoader().getResourceAsStream("textures/radio.png"), // icon resource location
                10 // weight
        ).build();

        voiceServer.getEventBus().register(this, new ActivationListener());

        voiceServer.getEventBus().register(this, new SourceListener());

        File addonFolder = new File(voiceServer.getMinecraftServer().getConfigsFolder(), "radio");

        voiceServer.getLanguages().register(
                this::getLanguageResource,
                new File(addonFolder, "languages") // folder where to store languages
        );

        PlasmoRadio.log("[===================]");
        PlasmoRadio.log("[ Addon initialized ]");
        PlasmoRadio.log("[===================]");
    }

    public ServerSourceLine getSourceLine() {
        return voiceServer.getSourceLineManager().createBuilder(
                this,
                "radio", // name
                "pv.activation.radio", // translation key
                getClass().getResourceAsStream("/textures/logo.png"), // icon resource location
                10 // weight
        ).build();
    }

    private InputStream getLanguageResource(String resourcePath) throws IOException {
        return getClass().getClassLoader().getResourceAsStream(String.format("pv-addon-radio/%s", resourcePath));
    }
}