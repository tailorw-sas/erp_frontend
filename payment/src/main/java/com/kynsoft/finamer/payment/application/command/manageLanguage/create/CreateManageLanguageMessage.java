package com.kynsoft.finamer.payment.application.command.manageLanguage.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageLanguageMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "CREATE_MANAGE_LANGUAGE_COMMAND";

    public CreateManageLanguageMessage(UUID id){
        this.id = id;
    }
}
