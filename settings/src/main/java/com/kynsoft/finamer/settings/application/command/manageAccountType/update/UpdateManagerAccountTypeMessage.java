package com.kynsoft.finamer.settings.application.command.manageAccountType.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManagerAccountTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGER_ACCOUNT_TYPE";

    public UpdateManagerAccountTypeMessage(UUID id) {
        this.id = id;
    }

}
