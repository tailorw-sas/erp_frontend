package com.kynsoft.finamer.creditcard.application.command.manageLanguage.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageLanguageMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGER_LANGUAGE";

    public UpdateManageLanguageMessage(UUID id) {
        this.id = id;
    }
}
