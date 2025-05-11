package org.ferrum.plasmoRadio.command;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ferrum.plasmoRadio.PlasmoRadio;
import su.plo.voice.api.server.player.VoicePlayer;

import static org.ferrum.plasmoRadio.utils.PlayerUtils.openFrequencyMenu;

public class SpeakRadioCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        sender.sendMessage("lox");
        openFrequencyMenu(player);
        return true;
    }
}