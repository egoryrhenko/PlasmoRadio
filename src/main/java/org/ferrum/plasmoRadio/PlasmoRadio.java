package org.ferrum.plasmoRadio;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.ferrum.plasmoRadio.command.ChangeCommand;
import org.ferrum.plasmoRadio.command.SpeakRadioCommand;
import org.ferrum.plasmoRadio.command.Test2;
import org.ferrum.plasmoRadio.listeners.InventoryClickListener;
import org.ferrum.plasmoRadio.listeners.RadioClickListener;
import org.ferrum.plasmoRadio.utils.ItemUtil;
import org.ferrum.plasmoRadio.utils.PlayerUtils;
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
        logger = getLogger();

        blockTypeKey = new NamespacedKey(this, "block_type");
        frequencyKey = new NamespacedKey(this, "frequency");

        addon = new RadioAddon();
        PlasmoVoiceServer.getAddonsLoader().load(addon);

        ItemUtil.init();

        getCommand("test").setExecutor(new Test2());
        getCommand("test").setTabCompleter(new Test2());

        getCommand("anvil").setExecutor(new SpeakRadioCommand());

        getCommand("change").setExecutor(new ChangeCommand());
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        getServer().getPluginManager().registerEvents(new RadioClickListener(), this);
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onDisable() {
        PlasmoVoiceServer.getAddonsLoader().unload(addon);
    }

    public static void log(String str) {
        logger.info("[DEV] "+str);
    }


}