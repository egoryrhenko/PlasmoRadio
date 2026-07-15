package org.ferrum.plasmoRadio.listeners.bukkit;


import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.ferrum.plasmoRadio.managers.ChunkStorageManager;
import org.ferrum.plasmoRadio.PlasmoRadio;
import org.ferrum.plasmoRadio.blocks.Locator;
import org.ferrum.plasmoRadio.blocks.Microphone;
import org.ferrum.plasmoRadio.blocks.Speaker;
import org.ferrum.plasmoRadio.utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockPlaceListener implements Listener {

    private final String defaultPassword = null;


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
            case 0 -> RadioDeviceRegistry.register(loc, new Microphone(loc));
            case 1 -> RadioDeviceRegistry.register(loc, new Speaker(loc));
            case 2 -> RadioDeviceRegistry.register(loc, new Locator(loc));
        }

        Map<String, String> options = new HashMap<>();
        options.put("frequency", "100.0");
        ChunkStorageManager.saveBlock(loc, RadioBlockType.fromId(type), options);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setDropItems(!breakRadioBlock(event.getBlock(), !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)));
    }

    @EventHandler
    public void onBlockBreak(EntityExplodeEvent event) {
        if (event.getEntity().getType().equals(EntityType.WIND_CHARGE)) return;
        explode(event.blockList());
    }

    @EventHandler
    public void onBlockBreak(BlockExplodeEvent event) {
        if (event.getExplodedBlockState().getType().equals(Material.AIR)) return;
        explode(event.blockList());
    }

    private void explode(List<Block> blockList) {
        for (Block block : blockList) {
            if (breakRadioBlock(block, true)) {
                block.setType(Material.AIR, false);
            }
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        explode(event.getBlocks());
    }

//    @EventHandler
//    public void onBlockDrop(BlockDropItemEvent event) {
//        Block block = event.getBlock();
//        if (isRadioBlock(block)) {
//            event.getItems().clear(); // удаляем весь дроп
//        }
//    }

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
            block.getWorld().dropItemNaturally(block.getLocation().add(new Vector(0.5f,0.5f,0.5f)), ItemUtil.getRadioItemStack(blockType));
        }

        RadioDeviceRegistry.get(loc).remove();
        RadioDeviceRegistry.unregister(loc);
        ChunkStorageManager.removeBlock(loc);
        return true;
    }
}