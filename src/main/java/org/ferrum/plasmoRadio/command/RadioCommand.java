package org.ferrum.plasmoRadio.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.ferrum.plasmoRadio.command.subcommands.GiveSubCommand;
import org.ferrum.plasmoRadio.command.subcommands.ReloadSubCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RadioCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (strings.length < 1) {
            commandSender.sendMessage(command.getUsage());
            return false;
        }

        switch (strings[0]) {
            case "reload" -> {
                ReloadSubCommand.run(commandSender, command, s, strings);
            }

            case "give" -> {
                GiveSubCommand.run(commandSender, command, s, strings);
                return true;
            }

            default -> {
                commandSender.sendMessage(command.getUsage());
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return List.of("reload", "give");
    }
}
