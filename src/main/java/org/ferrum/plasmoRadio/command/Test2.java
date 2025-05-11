package org.ferrum.plasmoRadio.command;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.ferrum.plasmoRadio.RadioAddon;
import org.ferrum.plasmoRadio.utils.RadioManager;
import org.ferrum.plasmoRadio.utils.Speaker;
import org.jetbrains.annotations.NotNull;
import su.plo.slib.api.server.position.ServerPos3d;
import su.plo.slib.api.server.world.McServerWorld;
import su.plo.voice.api.server.PlasmoVoiceServer;
import su.plo.voice.api.server.audio.line.ServerSourceLine;
import su.plo.voice.api.server.audio.source.ServerStaticSource;

import java.util.UUID;

public class Test2 implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (args.length != 1) {
            player.sendMessage("Используй: /radioblock <mic|speaker>");
            return false;
        }

        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        switch (args[0].toLowerCase()) {
            case "mic", "microphone" -> {
                meta.setDisplayName("§bМикрофон");
                meta.getPersistentDataContainer().set(new NamespacedKey("radioblock", "type"),
                        PersistentDataType.STRING, "mic");
            }
            case "speaker", "radio" -> {
                meta.setDisplayName("§aДинамик");
                meta.getPersistentDataContainer().set(new NamespacedKey("radioblock", "type"),
                        PersistentDataType.STRING, "speaker");
            }
            default -> {
                player.sendMessage("Неверный тип. Используй mic или speaker.");
                return false;
            }
        }

        item.setItemMeta(meta);
        player.getInventory().addItem(item);
        player.sendMessage("§aВыдан блок: " + args[0]);
        return true;
    }
}
