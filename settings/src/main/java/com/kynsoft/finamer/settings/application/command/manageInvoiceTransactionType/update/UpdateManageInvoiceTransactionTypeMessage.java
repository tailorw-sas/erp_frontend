package com.kynsoft.finamer.settings.application.command.manageInvoiceTransactionType.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageInvoiceTransactionTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_INVOICE_TRANSACTION_TYPE";

    public UpdateManageInvoiceTransactionTypeMessage(UUID id) {
        this.id = id;
    }
}
