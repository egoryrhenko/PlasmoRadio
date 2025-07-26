package org.ferrum.plasmoRadio.command;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.ferrum.plasmoRadio.PlasmoRadio;
import org.ferrum.plasmoRadio.RadioManager;
import org.ferrum.plasmoRadio.listeners.ChunkLoadListener;
import org.jetbrains.annotations.NotNull;

public class CheckCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        commandSender.sendMessage(Component.text("Задач загрузки "+ ChunkLoadListener.CountLoadTask));
        commandSender.sendMessage(Component.text("Задач отгрузки "+ ChunkLoadListener.CountUnloadTask));

        //PlasmoRadio.log("Задач загрузки "+ ChunkLoadListener.CountLoadTask);
        //PlasmoRadio.log("Задач отгрузки "+ ChunkLoadListener.CountUnloadTask);
        return true;
    }
}
