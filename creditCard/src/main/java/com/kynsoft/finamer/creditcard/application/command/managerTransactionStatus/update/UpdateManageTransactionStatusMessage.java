package com.kynsoft.finamer.creditcard.application.command.managerTransactionStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageTransactionStatusMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_TRANSACTION_STATUS";

    public UpdateManageTransactionStatusMessage(UUID id) {
        this.id = id;
    }

}
