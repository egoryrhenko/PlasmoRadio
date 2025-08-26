package org.ferrum.plasmoRadio.listeners;


import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.ferrum.plasmoRadio.DatabaseManager;
import org.ferrum.plasmoRadio.PlasmoRadio;
import org.ferrum.plasmoRadio.blocks.Locator;
import org.ferrum.plasmoRadio.blocks.Microphone;
import org.ferrum.plasmoRadio.blocks.Speaker;
import org.ferrum.plasmoRadio.utils.*;

public class BlockPlaceListener implements Listener {


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (notHead(block)) return;

        ItemMeta meta = event.getItemInHand().getItemMeta();
        if (meta == null) return;

        Byte type = meta.getPersistentDataContainer().get(PlasmoRadio.blockTypeKey, PersistentDataType.BYTE);
        if (type == null) return;

        BlockState state = block.getState();
        if (!(state instanceof Skull skull)) return;

        skull.getPersistentDataContainer().set(PlasmoRadio.blockTypeKey, PersistentDataType.BYTE, type);
        skull.update();

        Location loc = block.getLocation();

        switch (type) {
            case 0 -> RadioDeviceRegistry.register(loc, new Microphone(loc, 0f));
            case 1 -> RadioDeviceRegistry.register(loc, new Speaker(loc, 0f));
            case 2 -> RadioDeviceRegistry.register(loc, new Locator(loc, 0f));
        }

        DatabaseManager.saveBlock(loc, type, 0f);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setDropItems(!breakRadioBlock(event.getBlock(), !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)));
    }

    @EventHandler
    public void onBlockBreak(BlockExplodeEvent event) {
        breakRadioBlock(event.getBlock(), false);
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            breakRadioBlock(block, false);
        }
    }

    private boolean notHead(Block block) {
        Material type = block.getType();
        return type != Material.PLAYER_HEAD && type != Material.PLAYER_WALL_HEAD;
    }


    public boolean breakRadioBlock(Block block, boolean drop) {
        if (notHead(block)) return false;

        BlockState state = block.getState();
        if (!(state instanceof Skull skull)) return false;

        if (!skull.getPersistentDataContainer().has(PlasmoRadio.blockTypeKey)) return false;

        RadioBlockType blockType = RadioBlockType.fromId(skull.getPersistentDataContainer().get(PlasmoRadio.blockTypeKey, PersistentDataType.BYTE));

        Location loc = block.getLocation();

        if (drop) {
            block.getWorld().dropItemNaturally(block.getLocation().add(new Vector(0.5f,0.5f,0.5f)), ItemUtil.createCustomBlock(blockType,0));
        }

        RadioDeviceRegistry.get(loc).remove();
        RadioDeviceRegistry.unregister(loc);
        DatabaseManager.removeBlock(loc);
        return true;
    }
}