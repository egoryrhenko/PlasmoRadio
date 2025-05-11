package org.ferrum.plasmoRadio;

import org.bukkit.plugin.java.JavaPlugin;
import org.ferrum.plasmoRadio.command.SpeakRadioCommand;
import org.ferrum.plasmoRadio.command.Test2;
import org.ferrum.plasmoRadio.listeners.InventoryClickListener;
import su.plo.voice.api.server.PlasmoVoiceServer;

import java.util.logging.Logger;

public final class PlasmoRadio extends JavaPlugin {

    private static Logger logger;

    // Addon class annotated with @Addon.
    private final RadioAddon addon = new RadioAddon();

    @Override
    public void onEnable() {
        logger = getLogger();
        getCommand("test").setExecutor(new Test2());
        getCommand("anvil").setExecutor(new SpeakRadioCommand());
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
    }

    @Override
    public void onLoad() {
        PlasmoVoiceServer.getAddonsLoader().load(addon);
    }

    @Override
    public void onDisable() {
        PlasmoVoiceServer.getAddonsLoader().unload(addon);
    }

    public static void log(String str) {
        logger.info("[DEV] "+str);
    }


}