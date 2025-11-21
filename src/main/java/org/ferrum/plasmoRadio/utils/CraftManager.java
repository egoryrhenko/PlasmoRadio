package org.ferrum.plasmoRadio.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.ferrum.plasmoRadio.PlasmoRadio;
import org.ferrum.plasmoRadio.listeners.plasmovoice.PlayerConnectListener;

public class CraftManager {

    public static void addRecipe(String key , String[] shape, ConfigurationSection ingredientSection) {

        NamespacedKey namespacedKey = new NamespacedKey(PlasmoRadio.plugin, key.toLowerCase());
        PlayerConnectListener.recipeKeys.add(key.toLowerCase());
        Bukkit.removeRecipe(namespacedKey);

        ItemStack result = ItemUtil.getRadioItemStack(RadioBlockType.fromName(key));

        ShapedRecipe recipe = new ShapedRecipe(namespacedKey, result);
        recipe.shape(shape);

        for (String symbol : ingredientSection.getKeys(false)) {
            String id = (String) ingredientSection.get(symbol);
            if (id == null) {
                PlasmoRadio.plugin.getLogger().warning("§not found material in recipe '" + key + "'");
                continue;
            }
            if (id.startsWith("#")) {
                String tagName = id.substring(1).trim();
                NamespacedKey tagKey = NamespacedKey.minecraft(tagName);
                Tag<Material> tag = Bukkit.getTag(Tag.REGISTRY_BLOCKS, tagKey, Material.class);

                if (tag == null) {
                    PlasmoRadio.log("§eUnknown material tag '" + id + "' in recipe '" + key + "'");
                    continue;
                }

                recipe.setIngredient(symbol.charAt(0), new RecipeChoice.MaterialChoice(tag));
                continue;
            }
            Material material = Material.matchMaterial(id.toUpperCase());
            if (material  == null) {
                PlasmoRadio.plugin.getLogger().warning("§eUnknown material '" + id + "' in recipe '" + key + "'");
                continue;
            }
            recipe.setIngredient(symbol.charAt(0), material);
        }

        // Регистрируем рецепт
        Bukkit.addRecipe(recipe);
        PlasmoRadio.log("§aLoaded recipe: " + key);
    }
}
