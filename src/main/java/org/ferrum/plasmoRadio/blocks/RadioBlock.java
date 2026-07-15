package org.ferrum.plasmoRadio.blocks;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.ferrum.plasmoRadio.managers.ChunkStorageManager;
import org.ferrum.plasmoRadio.menu.RadioBlockMenu;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public abstract class RadioBlock {
    public Location location;
    public float frequency;
    public String password;
    public Boolean usePassword = false;
    protected final HashSet<UUID> sessions = new HashSet<>();

    public static float MIN_FREQUENCY = 80;
    public static float MAX_FREQUENCY = 140;

    public void openSettingsMenu(Audience audience) {
        if (!usePassword || password == null) {
            showSettingsMenu(audience);
        } else {
            if (audience instanceof Player player && sessions.contains(player.getUniqueId())) {
                showSettingsMenu(audience);
                return;
            }
            RadioBlockMenu.showEnterPasswordDialog(audience, this);
        }
    }

    public void showSettingsMenu(Audience audience) {
        RadioBlockMenu.showRadioBlockMenuDialog(audience, this);
    }

    public void enterPassword(Audience audience, String password) {
        if (this.password == null || !this.password.equals(password)) {
            audience.sendMessage(Component.text("Неверный пароль", NamedTextColor.RED));
            return;
        }
        if (audience instanceof Player player) {
            sessions.add(player.getUniqueId());
        }
        showSettingsMenu(audience);
    }
    public void changePassword(Audience audience, String oldPassword, String newPassword) {
        if (this.password != null && !this.password.equals(oldPassword)) {
            audience.sendMessage(Component.text("Неверный пароль", NamedTextColor.RED));
            return;
        }

        if (newPassword == null || newPassword.isBlank()) {
            audience.sendMessage(Component.text("Пароль не может быть пустым", NamedTextColor.RED));
            return;
        }
        this.password = newPassword;

        sessions.clear();
        if (audience instanceof Player player) {
            sessions.add(player.getUniqueId());
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
        ChunkStorageManager.updateOptions(location, getOptions());
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
        return Math.clamp(input, MIN_FREQUENCY, MAX_FREQUENCY);
    }

    public void switchUsePassword() {
        usePassword = !usePassword;
    }
    public abstract void changeFrequency(Float newFrequency);
    public abstract void remove();

}
