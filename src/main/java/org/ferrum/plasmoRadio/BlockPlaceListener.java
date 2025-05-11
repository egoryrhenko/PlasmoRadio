package org.ferrum.plasmoRadio;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.ferrum.plasmoRadio.utils.Microphone;
import org.ferrum.plasmoRadio.utils.RadioAudioForwarder;
import org.ferrum.plasmoRadio.utils.RadioManager;
import org.ferrum.plasmoRadio.utils.Speaker;

public class BlockPlaceListener implements Listener {

    private final NamespacedKey key;

    public BlockPlaceListener() {
        this.key = new NamespacedKey("radioblock", "type");
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (!isHead(block)) return;

        ItemStack item = event.getItemInHand();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        String type = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if (type == null) return;

        BlockState state = block.getState();
        if (!(state instanceof Skull skull)) return;

        skull.getPersistentDataContainer().set(key, PersistentDataType.STRING, type);
        skull.update();

        Location loc = block.getLocation();

        switch (type) {
            case "mic" -> {
                RadioManager.microphones.put(loc, new Microphone(loc));
                event.getPlayer().sendMessage("§bУстановлен микрофон.");
            }
            case "speaker" -> {
                RadioManager.speakers.put(loc, new Speaker(loc));

                event.getPlayer().sendMessage("§aУстановлен динамик.");
                event.getPlayer().sendMessage(RadioManager.speakers.size()+" s");
                event.getPlayer().sendMessage(RadioManager.microphones.size()+" n");
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!isHead(block)) return;

        BlockState state = block.getState();
        if (!(state instanceof Skull skull)) return;

        String type = skull.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if (type == null) return;

        Location loc = block.getLocation();

        switch (type) {
            case "mic" -> {
                RadioManager.microphones.remove(loc);
                event.getPlayer().sendMessage("§cМикрофон удалён.");
            }
            case "speaker" -> {
                RadioManager.speakers.get(loc).remove();
                RadioManager.speakers.remove(loc);
                event.getPlayer().sendMessage("§cДинамик удалён.");
            }
        }
    }

    private boolean isHead(Block block) {
        Material type = block.getType();
        return type == Material.PLAYER_HEAD || type == Material.PLAYER_WALL_HEAD;
    }
}