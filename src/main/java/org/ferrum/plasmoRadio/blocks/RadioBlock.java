package org.ferrum.plasmoRadio.blocks;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.ferrum.plasmoRadio.PlasmoRadio;
import org.ferrum.plasmoRadio.managers.DatabaseManager;
import org.ferrum.plasmoRadio.menu.RadioBlockMenu;
import su.plo.voice.api.server.audio.source.ServerStaticSource;
import su.plo.voice.api.server.player.VoicePlayer;
import su.plo.voice.proto.packets.udp.clientbound.SourceAudioPacket;
import su.plo.voice.proto.packets.udp.serverbound.PlayerAudioPacket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class RadioBlock {
    public Location location;
    public float frequency;
    public String password;
    public Boolean usePassword = false;
    private final HashSet<String> sessions = new HashSet<>();

    public static float MIN_FREQUENCY = 80;
    public static float MAX_FREQUENCY = 140;

    public void openSettingsMenu(Audience audience) {
        if (usePassword == false || password == null) {
            RadioBlockMenu.showRadioBlockMenuDialog(audience, this);
        } else {
            if (audience instanceof Player player && sessions.contains(player.getName())) {

                RadioBlockMenu.showRadioBlockMenuDialog(audience, this);
                return;
            }
            RadioBlockMenu.showEnterPasswordDialog(audience, this);
        }

    }
    public void enterPassword(Audience audience, String password) {
        if (!this.password.equals(password)) {
            audience.sendMessage(Component.text("Неверный пароль", NamedTextColor.RED));
            return;
        }
        if (audience instanceof Player player) {
            sessions.add(player.getName());
        }
        RadioBlockMenu.showRadioBlockMenuDialog(audience, this);
    }
    public void changePassword(Audience audience, String oldPassword, String newPassword) {
        if (this.password != null && !this.password.equals(oldPassword)) {
            audience.sendMessage(Component.text("Неверный пароль", NamedTextColor.RED));
            return;
        }

        if (newPassword == null) {
            audience.sendMessage(Component.text("Не безопасный пароль", NamedTextColor.RED));
            return;
        }
        this.password = newPassword;

        sessions.clear();
        if (audience instanceof Player player) {
            PlasmoRadio.log(player.getName());
            sessions.add(player.getName());
            PlasmoRadio.log(sessions.size() +"");
        }
        saveOptions();
    }

    public Map<String, String> getOptions() {
        Map<String, String> options = new HashMap<>();
        options.put("frequency", String.valueOf(frequency));
        options.put("password", password);
        options.put("use_password", String.valueOf(usePassword));
        return options;
    }

    public void saveOptions() {
        DatabaseManager.updateOptions(location, getOptions());
    }

    public void loadOptions(Map<String, String> options) {
        if (options.containsKey("frequency")) {
            this.frequency = Float.parseFloat(options.get("frequency"));
        }
        if (options.containsKey("password")) {
            this.password = options.get("password");
        }
        if (options.containsKey("use_password")) {
            this.usePassword = Boolean.parseBoolean(options.get("use_password"));
        }

    }

    public float validFrequency(float input) {
        return Math.min(MAX_FREQUENCY, Math.max(MIN_FREQUENCY, input));
    }

    public void switchUsePassword() {
        usePassword = !usePassword;
        saveOptions();
    }
    public abstract void changeFrequency(Float newFrequency);
    public abstract void remove();

}
