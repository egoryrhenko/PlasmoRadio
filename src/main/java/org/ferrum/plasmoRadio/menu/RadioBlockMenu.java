package org.ferrum.plasmoRadio.menu;


import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.ferrum.plasmoRadio.blocks.RadioBlock;

import java.util.ArrayList;
import java.util.List;

public class RadioBlockMenu {
    public static void showRadioBlockMenuDialog(Audience audience, RadioBlock radioBlock) {
        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Настройки"))
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
                                        .width(300)
                                        .labelFormat("%1$s %2$s FM")
                                        .build()
                                )
                        )
                        .build()
                )

                .type(
                        DialogType.multiAction(
                                List.of(
                                        MenuHandler.usePasswordButton(radioBlock),
                                        MenuHandler.changePasswordButton(radioBlock),
                                        MenuHandler.saveSettingsButton(radioBlock),
                                        MenuHandler.cancelButton()
                                )
                        ).build()
                )
        );

        audience.showDialog(dialog);
    }

    public static void showChangePasswordDialog(Audience audience, RadioBlock radioBlock) {

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

        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Настройки -> пароль"))
                        .body(List.of(DialogBody.plainMessage(Component.text("\n\n\n\n"))))
                        .inputs(dialogInputs)
                        .build()
                )

                .type(
                        DialogType.confirmation(
                                MenuHandler.savePasswordButton(radioBlock),
                                MenuHandler.cancelChangePasswordButton(radioBlock)
                        ))
        );

        audience.showDialog(dialog);
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
}
