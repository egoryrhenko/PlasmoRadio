package org.ferrum.plasmoRadio.menu;


import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.ferrum.plasmoRadio.blocks.Microphone;
import org.ferrum.plasmoRadio.blocks.RadioBlock;
import org.ferrum.plasmoRadio.blocks.Speaker;
import org.ferrum.plasmoRadio.utils.RadioBlockType;

import java.util.ArrayList;
import java.util.List;

public class RadioBlockMenu {

    public static MiniMessage miniMessage = MiniMessage.miniMessage();

    private static DialogBase settingBase(RadioBlock radioBlock){
        try {
            return DialogBase.builder(Component.text("Настройки"))
                    .body(List.of(DialogBody.plainMessage(Component.text("\n\n\n\n\n\n\n\n"))))
                    .inputs(
                            List.of(
                                    // Ползунок "Частота"
                                    DialogInput.numberRange(
                                                    "frequency",
                                                    Component.text("Частота:"),
                                                    80f,
                                                    140f
                                            )
                                            .step(0.5f)
                                            .initial(radioBlock.frequency)
                                            .width(250)
                                            .labelFormat("%1$s %2$s FM")
                                            .build()
                            )
                    ).build();
        } catch (Exception e) {
            e.printStackTrace();
            if (radioBlock.frequency == 100f) {
                throw new RuntimeException();
            }
            radioBlock.frequency = 100f;
            return settingBase(radioBlock);
        }

    }


    private static DialogType createButton(List<ActionButton> buttons) {
        return DialogType.multiAction(buttons).columns(1).build();
    }


    public static void showRadioBlockMenuDialog(Audience audience, RadioBlock radioBlock) {
        Dialog dialog = Dialog.create(builder -> builder.empty()
            .base(settingBase(radioBlock))
            .type(createButton(List.of(
                MenuHandler.passwordMenuButton(radioBlock),
                MenuHandler.soundButton(radioBlock),
                MenuHandler.saveSettingsButton(radioBlock))))
        );

        audience.showDialog(dialog);
    }

    public static void showMicrophoneMenuDialog(Audience audience, Microphone radioBlock) {
        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(settingBase(radioBlock))
                .type(createButton(List.of(
                                        MenuHandler.usePasswordButton(radioBlock),
                                        MenuHandler.changePasswordButton(radioBlock),
                                        MenuHandler.saveSettingsButton(radioBlock),
                                        MenuHandler.openKeySettingsButton(radioBlock)
                                )
                        )
                )
        );

        audience.showDialog(dialog);
    }

    public static Dialog PasswordSettingsDialog(RadioBlock radioBlock) {
        return Dialog.create(builder -> {
            builder.empty()
                .base(
                    DialogBase.builder(Component.text("Настройки -> пароль"))
                        .body(List.of(DialogBody.plainMessage(Component.text("\n\n\n\n\n\n\n\n"))))
                        .build()
                ).type(
                    DialogType.multiAction(
                            List.of(
                                    MenuHandler.changePasswordButton(radioBlock),
                                    MenuHandler.usePasswordButton(radioBlock),
                                    MenuHandler.mainMenuButton(radioBlock)
                            )
                    ).columns(1).build()
            );
        });

    }

    public static Dialog ChangePasswordDialog(RadioBlock radioBlock) {

        ArrayList<DialogInput> dialogInputs = new ArrayList<>();

        if (radioBlock.password != null) {
            dialogInputs.add(
                    DialogInput.text("old_password",
                            200,
                            Component.text("Введите старый пароль"),
                            true,
                            "",
                            16,
                            null
                    )
            );
        }

        dialogInputs.add(
                DialogInput.text("new_password",
                        200,
                        Component.text("Введите новый пароль"),
                        true,
                        "",
                        16,
                        null
                )
        );

        return Dialog.create(builder -> {
            builder.empty()
                .base(
                    DialogBase.builder(Component.text("Настройки -> пароль -> " + (radioBlock.password == null ? "установка " : "смена") + " пароля"))
                        .body(List.of(DialogBody.plainMessage(Component.text("\n\n\n\n"))))
                        .inputs(dialogInputs)
                        .build())
                .type(
                    DialogType.confirmation(
                        MenuHandler.savePasswordButton(radioBlock),
                        MenuHandler.cancelChangePasswordButton(radioBlock)
                ));
        });
    }

    public static void showEnterPasswordDialog(Audience audience, RadioBlock radioBlock) {
        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Настройки"))
                        .body(List.of(DialogBody.plainMessage(Component.text("\n\n\n\n"))))
                        .inputs(
                                List.of(
                                    DialogInput.text("password",
                                            200,
                                            Component.text("Пароль:"),
                                            true,
                                            "",
                                            16,
                                            null
                                    )
                                )
                        )
                        .build()
                )

                .type(
                        DialogType.confirmation(
                                MenuHandler.enterPasswordButton(radioBlock),
                                MenuHandler.cancelButton()
                        ))
        );

        audience.showDialog(dialog);

    }

    public static void showEnterKeyDialog(Audience audience, Microphone radioBlock) {
        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Настройки -> ключ"))
                        .body(List.of(DialogBody.plainMessage(Component.text("\n\n\n\n"))))
                        .inputs(
                                List.of(
                                    DialogInput.text("key",
                                            200,
                                            Component.text("Ключ:"),
                                            true,
                                            radioBlock.key == null ? "" : radioBlock.key,
                                            16,
                                            null
                                    )
                                )
                        )
                        .build()
                )

                .type(
                        DialogType.confirmation(
                                MenuHandler.enterKeyButton(radioBlock),
                                MenuHandler.cancelButton()
                        ))
        );

        audience.showDialog(dialog);

    }
}
