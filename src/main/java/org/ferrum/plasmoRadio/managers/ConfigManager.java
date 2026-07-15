package org.ferrum.plasmoRadio.managers;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.ferrum.plasmoRadio.PlasmoRadio;
import org.ferrum.plasmoRadio.listeners.plasmovoice.PlayerConnectListener;
import org.ferrum.plasmoRadio.utils.CraftManager;

import java.io.File;

public class ConfigManager {

    private static FileConfiguration config;

    private static volatile boolean radioEffectEnabled = false;
    private static volatile float radioEffectIntensity = 0.0f;

    public static boolean loadConfig() {
        try {

            File configFile = new File(PlasmoRadio.plugin.getDataFolder(), "config.yml");
            if (!configFile.exists()) {
                PlasmoRadio.plugin.saveResource("config.yml", false);
            }

            config = YamlConfiguration.loadConfiguration(configFile);

            radioEffectEnabled = config.getBoolean("radio-effect.enabled", false);
            radioEffectIntensity = Math.min(1.0f, Math.max(0.0f,
                    (float) config.getDouble("radio-effect.intensity", 0.0)));

            loadRecipes();

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean isRadioEffectEnabled() {
        return radioEffectEnabled;
    }

    public static float getRadioEffectIntensity() {
        return radioEffectIntensity;
    }

    public static void loadRecipes() throws Exception {
        ConfigurationSection recipes = config.getConfigurationSection("Recipes");
        if (recipes == null) {
            throw new Exception("No 'Recipes' section found in config.yml");
        }

        PlayerConnectListener.recipeKeys.clear();

        for (String key : recipes.getKeys(false)) {
            PlasmoRadio.log("load recipe: "+key);
            ConfigurationSection section = recipes.getConfigurationSection(key);
            if (section == null) continue;

            int gridSize = section.getInt("grid_size", 3);
            ConfigurationSection shapeSec = section.getConfigurationSection("shape");
            ConfigurationSection ingredientSection = section.getConfigurationSection("ingredient");


            if (gridSize < 1 || gridSize > 3) {
                throw new Exception("value \"" + key + " -> grid_size\" not in range 1-3");
            }

            String[] shape = new String[gridSize];

            for (int i = 0; i < gridSize; i++) {
                if (shapeSec == null){
                    throw new Exception("Error in config, line \"Debug_stick_craft -> shape\" not found");
                }
                String line = shapeSec.getString("line" + (i + 1) );
                if (line == null){
                    throw new Exception("Error in config, line \"Debug_stick_craft -> shape -> line" + (i+1) + "\" not found");
                }
                if (line.length()!=gridSize) {
                    throw new Exception("Error in config, line length \"Debug_stick_craft -> shape -> line" + (i+1) + "\" not equal to " + gridSize +" characters");
                }
                shape[i] = line;
            }

            if (ingredientSection == null){
                throw new Exception("Error in config, line \"Debug_stick_craft -> ing\" not found");
            }

            CraftManager.addRecipe(key, shape, ingredientSection);
        }
    }

}

