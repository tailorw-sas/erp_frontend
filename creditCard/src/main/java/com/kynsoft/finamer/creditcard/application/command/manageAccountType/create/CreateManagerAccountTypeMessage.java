package com.kynsoft.finamer.creditcard.application.command.manageAccountType.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManagerAccountTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGER_ACCOUNT_TYPE";

    public CreateManagerAccountTypeMessage(UUID id) {
        this.id = id;
    }

}
