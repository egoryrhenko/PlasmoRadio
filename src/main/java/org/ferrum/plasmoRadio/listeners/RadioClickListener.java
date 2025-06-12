package org.ferrum.plasmoRadio.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.ferrum.plasmoRadio.utils.Microphone;
import org.ferrum.plasmoRadio.utils.PlayerUtils;
import org.ferrum.plasmoRadio.utils.RadioManager;
import org.ferrum.plasmoRadio.utils.Speaker;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Objects;

public class RadioClickListener implements Listener {

    @EventHandler
    public void onRightClickBlock(PlayerInteractEvent event) {
        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        Location clickLoc = Objects.requireNonNull(event.getClickedBlock()).getLocation();

        if (RadioManager.microphones.containsKey(clickLoc)) {
            Microphone microphone = RadioManager.microphones.get(clickLoc);

            PlayerUtils.openFrequencyMenu(event.getPlayer(), toString(microphone.frequency), (frequency -> {
                microphone.frequency = frequency;
                microphone.test();
            }));

        }
        if (RadioManager.speakers.containsKey(clickLoc)) {
            Speaker speaker = RadioManager.speakers.get(clickLoc);

            PlayerUtils.openFrequencyMenu(event.getPlayer(), toString(speaker.frequency), (frequency -> {
                speaker.frequency = frequency;
                speaker.test();
            }));

        }


    }

    public static String toString(float value) {
        return new DecimalFormat("0.##").format(value);
    }

}
