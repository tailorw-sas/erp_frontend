package com.kynsoft.finamer.creditcard.application.command.managerTransactionStatus.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManageTransactionStatusMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGE_TRANSACTION_STATUS";

    public DeleteManageTransactionStatusMessage(UUID id) {
        this.id = id;
    }

}
