package org.ferrum.plasmoRadio;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.ferrum.plasmoRadio.command.CheckCommand;
import org.ferrum.plasmoRadio.command.FixCommand;
import org.ferrum.plasmoRadio.command.Test2;
import org.ferrum.plasmoRadio.listeners.BlockPlaceListener;
import org.ferrum.plasmoRadio.listeners.ChunkLoadListener;
import org.ferrum.plasmoRadio.listeners.RadioClickListener;
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

        DatabaseManager.init();

        blockTypeKey = new NamespacedKey(this, "block_type");
        frequencyKey = new NamespacedKey(this, "frequency");

        ItemUtil.init();

        getCommand("test").setExecutor(new Test2());
        getCommand("test").setTabCompleter(new Test2());
        getCommand("test").setPermission("admin.admin");

        getCommand("check").setExecutor(new CheckCommand());
        getCommand("check").setPermission("admin.admin");

        getCommand("fix").setExecutor(new FixCommand());
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