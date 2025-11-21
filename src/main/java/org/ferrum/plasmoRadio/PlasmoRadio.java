package org.ferrum.plasmoRadio;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.ferrum.plasmoRadio.command.CheckCommand;
import org.ferrum.plasmoRadio.command.RadioCommand;
import org.ferrum.plasmoRadio.command.Test2;
import org.ferrum.plasmoRadio.listeners.bukkit.BlockPlaceListener;
import org.ferrum.plasmoRadio.listeners.bukkit.ChunkLoadListener;
import org.ferrum.plasmoRadio.listeners.bukkit.RadioClickListener;
import org.ferrum.plasmoRadio.managers.ConfigManager;
import org.ferrum.plasmoRadio.managers.DatabaseManager;
import org.ferrum.plasmoRadio.utils.ItemUtil;
import su.plo.voice.api.server.PlasmoVoiceServer;

import java.util.logging.Logger;

public final class PlasmoRadio extends JavaPlugin {

    public static PlasmoRadio plugin;

    public static NamespacedKey blockTypeKey;
    public static NamespacedKey frequencyKey;

    private static Logger logger;

    // Addon class annotated with @Addon.
    private RadioAddon addon;

    @Override
    public void onEnable() {
        plugin = this;

        blockTypeKey = new NamespacedKey(this, "block_type");
        frequencyKey = new NamespacedKey(this, "frequency");

        ItemUtil.init();
        ConfigManager.loadConfig();

        DatabaseManager.init();

        RadioCommand radioCommand = new RadioCommand();

        getCommand("radio").setExecutor(radioCommand);
        getCommand("radio").setTabCompleter(radioCommand);
        getCommand("radio").setPermission("admin.admin");

        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        getServer().getPluginManager().registerEvents(new RadioClickListener(), this);
        getServer().getPluginManager().registerEvents(new ChunkLoadListener(), this);
    }

    @Override
    public void onLoad() {
        logger = getLogger();
        addon = new RadioAddon();
        log(addon.toString());
        PlasmoVoiceServer.getAddonsLoader().load(addon);
        log("Произаол загруз");
    }

    @Override
    public void onDisable() {
        PlasmoVoiceServer.getAddonsLoader().unload(addon);
        //DatabaseManager
    }

    public static void log(String str) {
        logger.info("[DEV] "+str);
    }


}