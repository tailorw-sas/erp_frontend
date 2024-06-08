package com.kynsoft.finamer.settings.application.command.manageReconcileTransactionStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageReconcileTransactionStatusMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_RECONCILE_TRANSACTION_STATUS";

    public CreateManageReconcileTransactionStatusMessage(UUID id) {
        this.id = id;
    }
}
