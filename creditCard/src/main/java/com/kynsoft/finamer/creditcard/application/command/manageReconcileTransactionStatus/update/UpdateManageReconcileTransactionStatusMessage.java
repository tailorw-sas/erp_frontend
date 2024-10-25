package com.kynsoft.finamer.creditcard.application.command.manageReconcileTransactionStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageReconcileTransactionStatusMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_RECONCILE_TRANSACTION_STATUS";

    public UpdateManageReconcileTransactionStatusMessage(UUID id) {
        this.id = id;
    }
}
