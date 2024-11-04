package com.kynsoft.finamer.creditcard.application.command.manageReconcileTransactionStatus.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteManageReconcileTransactionStatusMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_RECONCILE_TRANSACTION_STATUS";

    public DeleteManageReconcileTransactionStatusMessage(UUID id) {
        this.id = id;
    }
}
