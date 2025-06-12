package org.ferrum.plasmoRadio.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.ferrum.plasmoRadio.PlasmoRadio;
import org.ferrum.plasmoRadio.RadioAddon;
import org.ferrum.plasmoRadio.utils.Microphone;
import org.ferrum.plasmoRadio.utils.RadioManager;
import org.ferrum.plasmoRadio.utils.Speaker;
import org.jetbrains.annotations.NotNull;
import su.plo.voice.api.server.audio.capture.ServerActivation;
import su.plo.voice.api.server.player.VoicePlayer;
import su.plo.voice.proto.packets.udp.clientbound.SourceAudioPacket;

public class ChangeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {


        ServerActivation activation = RadioAddon.getVoiceServer.getActivationManager()
                .getActivationByName("proximity")
                .orElseThrow(() -> new IllegalStateException("Proximity activation not found"));

        activation.onPlayerActivation(RadioManager.onActivation());

        activation.onPlayerActivationEnd(RadioManager.onActivationEnd());

        return true;
    }
}
