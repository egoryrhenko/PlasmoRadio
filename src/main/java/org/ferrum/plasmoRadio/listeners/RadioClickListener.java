package org.ferrum.plasmoRadio.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.ferrum.plasmoRadio.blocks.Locator;
import org.ferrum.plasmoRadio.blocks.Microphone;
import org.ferrum.plasmoRadio.blocks.RadioBlock;
import org.ferrum.plasmoRadio.utils.PlayerUtils;
import org.ferrum.plasmoRadio.RadioManager;
import org.ferrum.plasmoRadio.blocks.Speaker;
import org.ferrum.plasmoRadio.utils.RadioDeviceRegistry;

import java.text.DecimalFormat;
import java.util.Objects;

public class RadioClickListener implements Listener {

    @EventHandler
    public void onRightClickBlock(PlayerInteractEvent event) {
        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        RadioBlock radioBlock = RadioDeviceRegistry.get(Objects.requireNonNull(event.getClickedBlock()).getLocation());

        if (radioBlock == null) {
            return;
        }

        if (!event.getPlayer().isSneaking()) {
            if (radioBlock instanceof Locator locator) {
                locator.openMenu(event.getPlayer());
            }
            return;
        }



        PlayerUtils.openFrequencyMenu(event.getPlayer(), toString(radioBlock.frequency), (frequency -> {
            radioBlock.frequency = frequency;
            radioBlock.update();
        }));
    }

    public static String toString(float value) {
        return new DecimalFormat("0.##").format(value);
    }

}
