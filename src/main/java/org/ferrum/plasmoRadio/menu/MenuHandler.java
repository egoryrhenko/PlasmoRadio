package org.ferrum.plasmoRadio.menu;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.Bukkit;
import org.ferrum.plasmoRadio.blocks.RadioBlock;
import org.ferrum.plasmoRadio.managers.DatabaseManager;

public class MenuHandler {
    public static ActionButton createButton(String text, String tooltip, int width, DialogAction dialogAction) {
        return ActionButton.create(
                Component.text(text),
                tooltip == null ? null : Component.text(tooltip),
                width,
                dialogAction // просто закрывает диалог
        );
    }

    public static ActionButton usePasswordButton(RadioBlock radioBlock) {
        return createButton(
                "Требовать пароль: " + (radioBlock.usePassword ? "Да" : "Нет"),
                null,
                150,
                DialogAction.customClick((view, audience) -> {

                    radioBlock.changeFrequency(view.getFloat("frequency"));
                    radioBlock.switchUsePassword();

                    RadioBlockMenu.showRadioBlockMenuDialog(audience, radioBlock);
                }, ClickCallback.Options.builder()
                        .uses(1)
                        .build())
        );
    }

    public static ActionButton changePasswordButton(RadioBlock radioBlock) {
        return createButton(
                "Изменить пароль",
                null,
                150,
                DialogAction.customClick((view, audience) -> {
                    RadioBlockMenu.showChangePasswordDialog(audience, radioBlock);
                }, ClickCallback.Options.builder()
                        .uses(1)
                        .build())
        );
    }

    public static ActionButton cancelButton(){
        return createButton(
                "Отмена",
                null,
                150,
                null // просто закрывает диалог
        );
    }

    public static ActionButton cancelChangePasswordButton(RadioBlock radioBlock){
        return createButton(
                "Назад",
                null,
                150,
                DialogAction.customClick((view, audience) -> {
                    RadioBlockMenu.showRadioBlockMenuDialog(audience, radioBlock);
                }, ClickCallback.Options.builder().build()
        )// просто закрывает диалог
        );
    }
    public static ActionButton saveSettingsButton(RadioBlock radioBlock) {
        return createButton(
                "Сохранить настройки",
                null,
                150,
                DialogAction.customClick((view, audience) -> {
                    float freq = view.getFloat("frequency");

                    radioBlock.changeFrequency(freq);

                }, ClickCallback.Options.builder()
                        .uses(1)
                        .build())
                );
    }

    public static ActionButton savePasswordButton(RadioBlock radioBlock) {
        return createButton(
                "Сохранить",
                null,
                150,
                DialogAction.customClick((view, audience) -> {
                    String oldPassword = view.getText("old_password");
                    String newPassword = view.getText("new_password");

                    radioBlock.changePassword(audience, oldPassword, newPassword);
                }, ClickCallback.Options.builder()
                        .uses(1)
                        .build())
        );
    }

    public static ActionButton enterPasswordButton(RadioBlock radioBlock) {
        return createButton(
                "Войти",
                null,
                150,
                DialogAction.customClick((view, audience) -> {
                    String password = view.getText("password");

                    radioBlock.enterPassword(audience, password);
                }, ClickCallback.Options.builder()
                        .uses(1)
                        .build())
        );
    }
}
