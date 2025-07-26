package org.ferrum.plasmoRadio.blocks;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.ferrum.plasmoRadio.PlasmoRadio;
import org.ferrum.plasmoRadio.RadioManager;
import org.ferrum.plasmoRadio.utils.RadioDeviceRegistry;
import su.plo.voice.api.server.audio.source.ServerStaticSource;
import su.plo.voice.api.server.player.VoicePlayer;
import su.plo.voice.proto.packets.udp.clientbound.SourceAudioPacket;
import su.plo.voice.proto.packets.udp.serverbound.PlayerAudioPacket;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Locator extends RadioBlock {

    public Locator(Location location) {
        this.location = location;
        update();
    }
    @Override
    public void update() {
        for (Microphone microphone : RadioDeviceRegistry.getMicrophones()) {
            if (microphone.devices.contains(this) && microphone.frequency != this.frequency) {
                microphone.devices.remove(this);
                PlasmoRadio.log("remove l");
            }
            if (!microphone.devices.contains(this) && microphone.frequency == this.frequency) {
                microphone.devices.add(this);
                PlasmoRadio.log("add l");
            }
        }
    }

    private final Map<UUID, Instant> lastSpoken = new HashMap<>();

    public Map<UUID, Duration> getAllWhoSpokeWithTimeAgo() {
        Map<UUID, Duration> result = new HashMap<>();
        Instant now = Instant.now();
        for (Map.Entry<UUID, Instant> entry : lastSpoken.entrySet()) {
            Duration timeAgo = Duration.between(entry.getValue(), now);
            result.put(entry.getKey(), timeAgo);
        }
        return result;
    }

    public void openMenu(Player player) {
        Map<UUID, Duration> test = getAllWhoSpokeWithTimeAgo();
        Inventory menu = Bukkit.createInventory(player,27,Component.text("Menu"));
        int i = 0;
        for (Map.Entry<UUID, Duration> entry : test.entrySet()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(entry.getKey());
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            if (offlinePlayer.hasPlayedBefore()) {
                meta.displayName(Component.text(offlinePlayer.getName()));
                meta.setPlayerProfile(offlinePlayer.getPlayerProfile());
                meta.lore(List.of(
                        Component.empty()
                        .append(Component.text(player.getName()+": "))
                        .append(Component.text(entry.getValue().toSeconds())))
                );
            } else {
                meta.displayName(Component.text("Пластинка"));
                meta.setPlayerProfile(player.getPlayerProfile());
                meta.lore(List.of(
                        Component.empty()
                                .append(Component.text("Gkfcmnasd: "))
                                .append(Component.text(entry.getValue().toSeconds())))
                );
            }
            skull.setItemMeta(meta);
            menu.addItem(skull);
        }
        player.openInventory(menu);
    }

    @Override
    public void test(VoicePlayer player, PlayerAudioPacket packet) {
        lastSpoken.put(player.getInstance().getUuid(), Instant.now());
    }

    @Override
    public void test(ServerStaticSource staticSource, SourceAudioPacket packet) {
        lastSpoken.put(staticSource.getId(), Instant.now());
    }
    @Override
    public void remove() {
        for (Microphone microphone : RadioDeviceRegistry.getMicrophones()) {
            if (microphone.devices.contains(this)) {
                microphone.devices.remove(this);
                PlasmoRadio.log("remove fo delete L");
            }
        }
    }
}
