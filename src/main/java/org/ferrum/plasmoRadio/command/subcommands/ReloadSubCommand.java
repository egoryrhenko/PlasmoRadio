package org.ferrum.plasmoRadio.command.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.ferrum.plasmoRadio.managers.ConfigManager;
import org.jetbrains.annotations.NotNull;

public class ReloadSubCommand {
    public static void run(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (ConfigManager.loadConfig()) {
            commandSender.sendMessage("Конфиг загружен успешно");
        } else {
            commandSender.sendMessage("Ошибка в конфиги... ( Читать в источнике )");
        }
    }
}
