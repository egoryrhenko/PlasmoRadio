package org.ferrum.plasmoRadio.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.ferrum.plasmoRadio.RadioAddon;
import org.ferrum.plasmoRadio.RadioManager;
import org.jetbrains.annotations.NotNull;
import su.plo.voice.api.server.audio.capture.ServerActivation;

public class FixCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {


        ServerActivation activation = RadioAddon.staticVoiceServer.getActivationManager()
                .getActivationByName("proximity")
                .orElseThrow(() -> new IllegalStateException("Proximity activation not found"));

        activation.onPlayerActivation(RadioManager.onActivation());

        activation.onPlayerActivationEnd(RadioManager.onActivationEnd());

        commandSender.sendMessage("Ошибок нет но не факт что работает");

        return true;
    }
}
