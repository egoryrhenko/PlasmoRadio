package org.ferrum.plasmoRadio.listeners.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.ferrum.plasmoRadio.blocks.*;

public class LocatorMenuListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory().getHolder() instanceof Locator) {
            event.setCancelled(true);
        }
    }
}
