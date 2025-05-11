package org.ferrum.plasmoRadio.command;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ferrum.plasmoRadio.PlasmoRadio;
import su.plo.voice.api.server.player.VoicePlayer;

public class SpeakRadioCommand implements CommandExecutor {
    private final PlasmoRadio plugin;

    public SpeakRadioCommand(PlasmoRadio plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("lox");
        return true;
    }
}