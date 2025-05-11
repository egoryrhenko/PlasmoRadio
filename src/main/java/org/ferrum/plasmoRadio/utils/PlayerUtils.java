package org.ferrum.plasmoRadio.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerUtils {
    public static void openFrequencyMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, InventoryType.ANVIL, Component.text("Выбор частоты"));
        ItemStack item = new ItemStack(Material.PAPER);
        menu.setItem(0, item);
        player.openInventory(menu);
    }
}
