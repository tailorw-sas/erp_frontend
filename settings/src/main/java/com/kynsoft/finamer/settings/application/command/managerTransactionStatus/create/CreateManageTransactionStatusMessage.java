package com.kynsoft.finamer.settings.application.command.managerTransactionStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageTransactionStatusMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_TRANSACTION_STATUS";

    public CreateManageTransactionStatusMessage(UUID id) {
        this.id = id;
    }

}
