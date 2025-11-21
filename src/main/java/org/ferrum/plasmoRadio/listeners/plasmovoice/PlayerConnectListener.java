package org.ferrum.plasmoRadio.listeners.plasmovoice;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.ferrum.plasmoRadio.PlasmoRadio;
import su.plo.voice.api.event.EventSubscribe;
import su.plo.voice.api.server.event.connection.UdpClientConnectEvent;

import java.util.HashSet;

public class PlayerConnectListener {

    public static final HashSet<String> recipeKeys = new HashSet<>();

    @EventSubscribe
    public void onVoiceConnect(UdpClientConnectEvent event) {

        Player player = Bukkit.getPlayer(event.getConnection().getPlayer().getInstance().getUuid());

        if (player == null) return;

        for (String key : recipeKeys) {
            NamespacedKey recipe = new NamespacedKey(PlasmoRadio.plugin, key);
            if (!player.hasDiscoveredRecipe(recipe)) {
                player.discoverRecipe(recipe);
            }
        }
    }
}
