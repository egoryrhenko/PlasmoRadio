package org.ferrum.plasmoRadio.utils;

import net.kyori.adventure.text.Component;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ferrum.plasmoRadio.PlasmoRadio;

import java.util.Collections;
import java.util.function.Consumer;

public class PlayerUtils {
    public static void openFrequencyMenu(Player player, String input, Consumer<Float> callback) {
        new AnvilGUI.Builder()
                .plugin(PlasmoRadio.plugin)
                .title("Выберите частоту")
                .text(input)
                .onClick((clickEvent, text) -> {
                    try {
                        float value = Float.parseFloat(text.getText());
                        callback.accept(value); // возвращаем значение через callback
                        return Collections.singletonList(AnvilGUI.ResponseAction.close());
                    } catch (NumberFormatException e) {
                        player.sendMessage("§cВведите корректное число.");
                        return AnvilGUI.Response.text(text.getText()); // оставить поле
                    }
                })
                .open(player);
    }
}
