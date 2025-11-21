package org.ferrum.plasmoRadio.command.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.ferrum.plasmoRadio.utils.ItemUtil;
import org.ferrum.plasmoRadio.utils.RadioBlockType;
import org.jetbrains.annotations.NotNull;

public class GiveSubCommand {
    public static void run(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!(commandSender instanceof Player player)) {
            return;
        }
        Inventory inventory = Bukkit.createInventory(player,9,"Radio give menu");
        for (RadioBlockType type : RadioBlockType.values()) {
            inventory.addItem(ItemUtil.getRadioItemStack(type));
        }
        player.openInventory(inventory);
    }
}
