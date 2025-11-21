package org.ferrum.plasmoRadio.listeners.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.ferrum.plasmoRadio.blocks.Locator;
import org.ferrum.plasmoRadio.blocks.RadioBlock;
import org.ferrum.plasmoRadio.utils.RadioDeviceRegistry;

import java.util.Objects;

public class RadioClickListener implements Listener {

    @EventHandler
    public void onRightClickBlock(PlayerInteractEvent event) {
        if (!(event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            return;
        }

        Player player = event.getPlayer();
        RadioBlock radioBlock = RadioDeviceRegistry.get(Objects.requireNonNull(event.getClickedBlock()).getLocation());

        if (radioBlock == null ) {
            return;
        }

        if (!player.isSneaking()) {
            if (radioBlock instanceof Locator locator) {
                locator.openMenu(player);
            }
            return;
        }

        radioBlock.openSettingsMenu(event.getPlayer());
    }

}
