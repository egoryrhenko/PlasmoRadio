package org.ferrum.plasmoRadio.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ferrum.plasmoRadio.utils.RadioBlockType;
import org.ferrum.plasmoRadio.utils.ItemUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Test2 implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        if (args.length != 1) {
            player.sendMessage("Используй: /radioblock <mic|speaker>");
            return false;
        }

        ItemStack item;

        switch (args[0].toLowerCase()) {
            case "mic", "microphone" -> {
                item = ItemUtil.createCustomBlock(RadioBlockType.Microphone, 0);
            }
            case "speaker", "radio" -> {
                item = ItemUtil.createCustomBlock(RadioBlockType.Speaker, 0);
            }
            case "locator" -> {
                item = ItemUtil.createCustomBlock(RadioBlockType.Locator, 0);
            }
            default -> {
                player.sendMessage("Неверный тип. Используй mic или speaker.");
                return false;
            }
        }

        player.getInventory().addItem(item);
        player.sendMessage("§aВыдан блок: " + args[0]);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return List.of("mic", "speaker", "locator");
    }
}
