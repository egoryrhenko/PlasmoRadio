package org.ferrum.plasmoRadio.listeners.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.ferrum.plasmoRadio.blocks.Locator;
import org.ferrum.plasmoRadio.blocks.RadioBlock;
import org.ferrum.plasmoRadio.utils.RadioDeviceRegistry;

import java.util.Objects;

public class RadioClickListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onRightClickBlock(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // Only handle the main hand to avoid opening the menu twice.
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        RadioBlock radioBlock = RadioDeviceRegistry.get(Objects.requireNonNull(event.getClickedBlock()).getLocation());
        if (radioBlock == null) {
            return;
        }

        // Shift + right click always opens the settings dialog.
        if (player.isSneaking()) {
            event.setCancelled(true);
            radioBlock.openSettingsMenu(player);
            return;
        }

        // Normal right click opens the block's primary menu.
        event.setCancelled(true);
        if (radioBlock instanceof Locator locator) {
            locator.openMenu(player);
        } else {
            radioBlock.openSettingsMenu(player);
        }
    }

}
