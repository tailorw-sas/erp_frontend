package com.kynsoft.finamer.creditcard.application.command.manageAccountType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManagerAccountTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGER_ACCOUNT_TYPE";

    public DeleteManagerAccountTypeMessage(UUID id) {
        this.id = id;
    }

}
