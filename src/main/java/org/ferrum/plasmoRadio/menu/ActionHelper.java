package org.ferrum.plasmoRadio.menu;

import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.event.ClickEvent;
import org.ferrum.plasmoRadio.blocks.RadioBlock;

public class ActionHelper {

    public static DialogAction openPasswordMenu(RadioBlock radioBlock) {
        return DialogAction.staticAction(ClickEvent.showDialog(RadioBlockMenu.PasswordSettingsDialog(radioBlock)));
    }

    public static DialogAction changePassword(RadioBlock radioBlock) {
        return DialogAction.staticAction(ClickEvent.showDialog(RadioBlockMenu.ChangePasswordDialog(radioBlock)));
    }

}
