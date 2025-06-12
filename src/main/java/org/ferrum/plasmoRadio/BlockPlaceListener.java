package org.ferrum.plasmoRadio;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.ferrum.plasmoRadio.utils.*;

public class BlockPlaceListener implements Listener {


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (notHead(block)) return;

        ItemStack item = event.getItemInHand();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        Byte type = meta.getPersistentDataContainer().get(PlasmoRadio.blockTypeKey, PersistentDataType.BYTE);
        if (type == null) return;

        BlockState state = block.getState();
        if (!(state instanceof Skull skull)) return;

        skull.getPersistentDataContainer().set(PlasmoRadio.blockTypeKey, PersistentDataType.BYTE, type);
        skull.update();

        Location loc = block.getLocation();

        switch (type) {
            case 0 -> {
                RadioManager.microphones.put(loc, new Microphone(loc));
                event.getPlayer().sendMessage("§bУстановлен микрофон.");

                event.getPlayer().sendMessage(RadioManager.speakers.size()+" s");
                event.getPlayer().sendMessage(RadioManager.microphones.size()+" n");
            }
            case 1 -> {
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
        if (notHead(block)) return;

        BlockState state = block.getState();
        if (!(state instanceof Skull skull)) return;

        Byte type = skull.getPersistentDataContainer().get(PlasmoRadio.blockTypeKey, PersistentDataType.BYTE);
        if (type == null) return;

        Location loc = block.getLocation();

        event.setDropItems(false);


        switch (type) {
            case 0 -> {
                RadioManager.microphones.remove(loc);
                event.getPlayer().sendMessage("§cМикрофон удалён.");
                block.getWorld().dropItemNaturally(block.getLocation().add(new Vector(0.5f,0.5f,0.5f)), ItemUtil.createCustomBlock(BlockType.Microphone,0));
            }
            case 1 -> {
                RadioManager.speakers.get(loc).remove();
                RadioManager.speakers.remove(loc);
                event.getPlayer().sendMessage("§cДинамик удалён.");
                block.getWorld().dropItemNaturally(block.getLocation().add(new Vector(0.5f,0.5f,0.5f)), ItemUtil.createCustomBlock(BlockType.Speaker,0));
            }
        }
    }

    private boolean notHead(Block block) {
        Material type = block.getType();
        return type != Material.PLAYER_HEAD && type != Material.PLAYER_WALL_HEAD;
    }
}