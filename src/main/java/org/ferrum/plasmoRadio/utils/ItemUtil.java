package org.ferrum.plasmoRadio.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.ferrum.plasmoRadio.PlasmoRadio;
import org.ferrum.plasmoRadio.blocks.Locator;

import java.util.UUID;

public class ItemUtil {

    private final static PlayerProfile profileMicrophone = Bukkit.createProfile(UUID.fromString("f7044cbe-9f2b-4bbd-8873-a8ad52dcf694"));
    private final static PlayerProfile profileSpeaker = Bukkit.createProfile(UUID.fromString("f7044cbe-9f2b-4bbd-8873-a8ad52dcf694"));
    private final static PlayerProfile profileLocator = Bukkit.createProfile(UUID.fromString("f7044cbe-9f2b-4bbd-8873-a8ad52dcf694"));

    public static void init() {
        profileMicrophone.setProperty(new ProfileProperty("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjZjMWRhODhhOTFiZTZmZDFiMzc1YjNiNTQ2ZDI4Y2M0NTYxMGMxZWM5NGViOGJlOTgzMTdlYTVlMTI0OWY0ZSJ9fX0="));
        profileSpeaker.setProperty(new ProfileProperty("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ4YThjNTU4OTFkZWM3Njc2NDQ0OWY1N2JhNjc3YmUzZWU4OGEwNjkyMWNhOTNiNmNjN2M5NjExYTdhZiJ9fX0="));
        profileLocator.setProperty(new ProfileProperty("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmMxODMxMjZhYjBhZGFlMzJiZThhZGEyNGZlZWNjMWY5ODJjYjM3MjMzMTM4Y2RkYzYwM2ViY2VhNGY2NWYxZiJ9fX0="));
    }

    public static ItemStack createCustomBlock(RadioBlockType blockType, float frequency) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        meta.getPersistentDataContainer().set(PlasmoRadio.blockTypeKey, PersistentDataType.BYTE, blockType.getId());
        meta.getPersistentDataContainer().set(PlasmoRadio.frequencyKey, PersistentDataType.FLOAT, frequency);

        switch (blockType) {
            case Speaker -> {
                meta.setPlayerProfile(profileSpeaker);
                meta.displayName(Component.text("Динамик", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC,false));
            }
            case Microphone -> {
                meta.setPlayerProfile(profileMicrophone);
                meta.displayName(Component.text("Микрофон", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC,false));
            }
            case Locator -> {
                meta.setPlayerProfile(profileLocator);
                meta.displayName(Component.text("Locator", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC,false));
            }
        }

        skull.setItemMeta(meta);

        return skull;
    }
}
