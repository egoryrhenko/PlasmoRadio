package org.ferrum.plasmoRadio.blocks;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.ParserDirective;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.ferrum.plasmoRadio.utils.RadioDeviceRegistry;
import org.jetbrains.annotations.NotNull;
import su.plo.slib.api.server.position.ServerPos3d;
import su.plo.voice.api.server.audio.source.ServerStaticSource;
import su.plo.voice.api.server.player.VoicePlayer;
import su.plo.voice.proto.packets.udp.clientbound.SourceAudioPacket;
import su.plo.voice.proto.packets.udp.serverbound.PlayerAudioPacket;

import java.util.*;

public class Locator extends ReceiveRadioBlock implements InventoryHolder {

    final static TagResolver.Single single = TagResolver.resolver("clear", ParserDirective.RESET);

    private static MiniMessage miniMessage = MiniMessage.builder()
            .editTags(t -> t.resolver(single))
            .build();

    public Locator(Location location) {
        this.location = location;
        setFrequency(100f);
    }

    public Locator(Location location, Map<String, String> options) {
        this.location = location;
        loadOptions(options);
    }

    private final Map<UUID, LocatorLog> locatorLogs = new HashMap<>();

    public void openMenu(Player player) {
        Inventory menu = Bukkit.createInventory(this, 27, "Locator menu");
        for (Map.Entry<UUID, LocatorLog> entry : locatorLogs.entrySet()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(entry.getKey());
            String name = offlinePlayer.getName();
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            if (name != null && offlinePlayer.hasPlayedBefore()) {
                meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC,false));
                PlayerProfile profile = offlinePlayer.getPlayerProfile();
                if (profile != null) {
                    meta.setPlayerProfile(profile);
                }
                Location loc = entry.getValue().location;
                meta.lore(List.of(
                        miniMessage.deserialize( "Объект: " + name + "<head:" + name + ">"),
                        miniMessage.deserialize("<white>Координаты: " + loc.getX() + " " + loc.getY() + " " + loc.getZ()),
                        miniMessage.deserialize("<clear>Мир: " + loc.getWorld().getName()),
                        Component.text("Время: " + getFormatTime(System.currentTimeMillis() - entry.getValue().timestamp) + " назад", NamedTextColor.WHITE)
                ));
            } else {
                meta.displayName(Component.text("Пластинка").decoration(TextDecoration.ITALIC,false));
                Location loc = entry.getValue().location;
                meta.lore(List.of(
                        miniMessage.deserialize("Объект: Пластинка<sprite:'minecraft:items':'minecraft:item/music_disc_wait'>"),
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
    public void receivePackage(VoicePlayer player, byte[] data, long s) {
        Player bukkitPlayer = Bukkit.getPlayer(player.getInstance().getUuid());
        if (bukkitPlayer == null) {
            return;
        }
        LocatorLog log = new LocatorLog(bukkitPlayer.getLocation(), System.currentTimeMillis());
        locatorLogs.put(bukkitPlayer.getUniqueId(), log);
    }

    @Override
    public void receivePackage(ServerStaticSource staticSource, byte[] data, long s) {
        ServerPos3d loc = staticSource.getPosition();
        org.bukkit.World world = Bukkit.getWorld(loc.getWorld().getName());
        if (world == null) {
            return;
        }
        LocatorLog log = new LocatorLog(new Location(world, loc.getX(), loc.getY(), loc.getZ()), System.currentTimeMillis());
        locatorLogs.put(staticSource.getId(), log);
    }
    @Override
    public void remove() {

    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
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

        if (days > 0) result.append(days).append(days == 1 ? " День " : days < 5 ? " Дня " : " Дней ");
        if (hours > 0) result.append(hours).append(hours == 1 ? " Час " : hours < 5 ? " Часа " : " Часов ");
        if (minutes > 0) result.append(minutes).append(minutes == 1 ? " Минута " : minutes < 5 ? " Минуты " : " Минут ");
        if (secs > 0) result.append(secs).append(secs == 1 ? " Секунду " : secs < 5 ? " Секунды " : " Секунд ");

        if (result.isEmpty()) return "меньше секунды";

        return result.toString();
    }
}


