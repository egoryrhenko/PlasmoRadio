package org.ferrum.plasmoRadio.menu;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.ferrum.plasmoRadio.blocks.Microphone;
import org.ferrum.plasmoRadio.blocks.RadioBlock;

import java.time.temporal.TemporalAmount;

public class MenuHandler {

    public static final int buttonWidth = 250;

    public static ActionButton createButton(String text, String tooltip, int width, DialogAction dialogAction) {
        return ActionButton.create(
                Component.text(text),
                tooltip == null ? null : Component.text(tooltip),
                width,
                dialogAction
        );
    }

    public static ActionButton mainMenuButton(RadioBlock radioBlock) {
        return createButton(
                "Назад",
                null,
                buttonWidth,
                DialogAction.customClick((view, audience) -> {
                    RadioBlockMenu.showRadioBlockMenuDialog(audience, radioBlock);
                }, ClickCallback.Options.builder().build()));
    }

    public static ActionButton soundButton(RadioBlock radioBlock) {
        return createButton(
                "Звук",
                "Скоро...",
                buttonWidth,
                null

        );
    }

    public static ActionButton passwordMenuButton(RadioBlock radioBlock) {
        return createButton(
                "Пароль",
                null,
                buttonWidth,
                ActionHelper.openPasswordMenu(radioBlock)
        );
    }

    public static ActionButton usePasswordButton(RadioBlock radioBlock) {
        return createButton(
                "Требовать пароль: " + (radioBlock.usePassword ? "Да" : "Нет"),
                null,
                buttonWidth,
                DialogAction.customClick((view, audience) -> {
                    radioBlock.switchUsePassword();
                    radioBlock.saveOptions();
                    audience.showDialog(RadioBlockMenu.PasswordSettingsDialog(radioBlock));
                }, ClickCallback.Options.builder().build())
        );
    }

    public static ActionButton changePasswordButton(RadioBlock radioBlock) {
        return createButton(
                radioBlock.password == null ? "Установить пароль" : "Изменить пароль",
                null,
                buttonWidth,
                ActionHelper.changePassword(radioBlock)
        );
    }

    public static ActionButton cancelButton(){
        return createButton(
                "Отмена",
                null,
                buttonWidth,
                null // просто закрывает диалог
        );
    }

    public static ActionButton cancelChangePasswordButton(RadioBlock radioBlock){
        return createButton(
                "Назад",
                null,
                buttonWidth/2,
                DialogAction.customClick(((response, audience) -> {
                    audience.showDialog(RadioBlockMenu.PasswordSettingsDialog(radioBlock));
                }), ClickCallback.Options.builder().build())
        );
    }
    public static ActionButton saveSettingsButton(RadioBlock radioBlock) {
        return createButton(
                "Сохранить настройки",
                null,
                buttonWidth,
                DialogAction.customClick((view, audience) -> {
                    Float freq = view.getFloat("frequency");
                    if (freq != null) {
                        radioBlock.changeFrequency(freq);
                    }
                }, ClickCallback.Options.builder()
                        .uses(1)
                        .build())
                );
    }

    public static ActionButton savePasswordButton(RadioBlock radioBlock) {
        return createButton(
                "Сохранить",
                null,
                buttonWidth/2,
                DialogAction.customClick((view, audience) -> {
                    String oldPassword = view.getText("old_password");
                    String newPassword = view.getText("new_password");

                    radioBlock.changePassword(audience, oldPassword, newPassword);

                    audience.showDialog(RadioBlockMenu.PasswordSettingsDialog(radioBlock));
                }, ClickCallback.Options.builder().build())
        );
    }

    public static ActionButton enterPasswordButton(RadioBlock radioBlock) {
        return createButton(
                "Войти",
                null,
                buttonWidth,
                DialogAction.customClick((view, audience) -> {
                    String password = view.getText("password");

                    radioBlock.enterPassword(audience, password);
                }, ClickCallback.Options.builder()
                        .uses(1)
                        .build())
        );
    }

    public static ActionButton openKeySettingsButton(Microphone radioBlock) {
        return createButton(
                "Ключ",
                null,
                buttonWidth,
                DialogAction.customClick((view, audience) -> {
                    RadioBlockMenu.showEnterKeyDialog(audience, radioBlock);
                }, ClickCallback.Options.builder().build())
        );
    }

    public static ActionButton enterKeyButton(RadioBlock radioBlock) {
        return createButton(
                "Сохранить",
                null,
                buttonWidth,
                DialogAction.customClick((view, audience) -> {
                    if (radioBlock instanceof Microphone microphone) {
                        microphone.key = view.getText("key");
                        microphone.saveOptions();
                        microphone.showSettingsMenu(audience);
                    }
                }, ClickCallback.Options.builder().build())
        );
    }
}
