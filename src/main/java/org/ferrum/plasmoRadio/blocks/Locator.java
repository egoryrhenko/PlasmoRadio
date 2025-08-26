package org.ferrum.plasmoRadio.blocks;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.ferrum.plasmoRadio.utils.RadioDeviceRegistry;
import su.plo.slib.api.server.position.ServerPos3d;
import su.plo.voice.api.server.audio.source.ServerStaticSource;
import su.plo.voice.api.server.player.VoicePlayer;
import su.plo.voice.proto.packets.udp.clientbound.SourceAudioPacket;
import su.plo.voice.proto.packets.udp.serverbound.PlayerAudioPacket;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Locator extends RadioBlock {

    public Locator(Location location, float frequency) {
        this.location = location;
        this.frequency = frequency;
        update();
    }
    @Override
    public void update() {
        for (Microphone microphone : RadioDeviceRegistry.getMicrophones()) {
            if (microphone.devices.contains(this) && microphone.frequency != this.frequency) {
                microphone.devices.remove(this);
            }
            if (!microphone.devices.contains(this) && microphone.frequency == this.frequency) {
                microphone.devices.add(this);
            }
        }
    }

    private final Map<UUID, LocatorLog> locatorLogs = new HashMap<>();

    public void openMenu(Player player) {
        Inventory menu = Bukkit.createInventory(player,27,Component.text("Menu"));
        int i = 0;
        for (Map.Entry<UUID, LocatorLog> entry : locatorLogs.entrySet()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(entry.getKey());
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            if (offlinePlayer.hasPlayedBefore()) {
                meta.displayName(Component.text(offlinePlayer.getName()).decoration(TextDecoration.ITALIC,false));
                meta.setPlayerProfile(offlinePlayer.getPlayerProfile());
                Location loc = entry.getValue().location;
                meta.lore(List.of(
                        Component.text("Объект: " + player.getName(), NamedTextColor.WHITE),
                        Component.text("Координаты: " + loc.getX() + " " + loc.getY() + " " + loc.getZ(), NamedTextColor.WHITE),
                        Component.text("Мир: " + loc.getWorld().getName(), NamedTextColor.WHITE),
                        Component.text("Время: " + getFormatTime(System.currentTimeMillis() - entry.getValue().timestamp) + " назад", NamedTextColor.WHITE)
                ));
            } else {
                meta.displayName(Component.text("Пластинка").decoration(TextDecoration.ITALIC,false));
                //meta.setPlayerProfile(player.getPlayerProfile());
                Location loc = entry.getValue().location;
                meta.lore(List.of(
                        Component.text("Объект: Пластинка", NamedTextColor.WHITE),
                        Component.text("Координаты: " + loc.getX() + " " + loc.getY() + " " + loc.getZ(), NamedTextColor.WHITE),
                        Component.text("Мир: " + loc.getWorld().getName(), NamedTextColor.WHITE),
                        Component.text("Время: " + getFormatTime(System.currentTimeMillis() - entry.getValue().timestamp) + " назад", NamedTextColor.WHITE)
                ));
            }
            skull.setItemMeta(meta);
            menu.addItem(skull);
        }
        player.openInventory(menu);
    }

    @Override
    public void test(VoicePlayer player, PlayerAudioPacket packet) {
        OfflinePlayer bukkitPlayer = Bukkit.getOfflinePlayer(player.getInstance().getUuid());
        LocatorLog log = new LocatorLog(bukkitPlayer.getLocation(), System.currentTimeMillis());
        locatorLogs.put(bukkitPlayer.getUniqueId(), log);
    }

    @Override
    public void test(ServerStaticSource staticSource, SourceAudioPacket packet) {
        ServerPos3d loc = staticSource.getPosition();
        LocatorLog log = new LocatorLog(new Location(Bukkit.getWorld(loc.getWorld().getName()), loc.getX(), loc.getY(), loc.getZ()), System.currentTimeMillis());
        locatorLogs.put(staticSource.getId(), log);
    }
    @Override
    public void remove() {
        for (Microphone microphone : RadioDeviceRegistry.getMicrophones()) {
            if (microphone.devices.contains(this)) {
                microphone.devices.remove(this);
            }
        }
    }

    private record LocatorLog(Location location, long timestamp) {
    }

    private static String getFormatTime(long time) {

        time /= 1000;

        long days = time / 86400;
        long hours = (time % 86400) / 3600;
        long minutes = (time % 3600) / 60;
        long secs = time % 60;

        StringBuilder result = new StringBuilder();

        if (days > 0) result.append(days).append(days == 1 ? " День " : " Дней ");
        if (hours > 0) result.append(hours).append(hours == 1 ? " Час " : " Часов ");
        if (minutes > 0) result.append(minutes).append(minutes == 1 ? " Минута " : " Минут ");
        if (secs > 0) result.append(secs).append(secs == 1 ? " Секунду " : " Секунд ");

        if (result.isEmpty()) return "меньше секунды";

        return result.toString();
    }
}


