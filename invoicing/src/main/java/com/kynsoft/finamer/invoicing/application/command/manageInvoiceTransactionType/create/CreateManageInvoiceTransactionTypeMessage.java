package com.kynsoft.finamer.invoicing.application.command.manageInvoiceTransactionType.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageInvoiceTransactionTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_INVOICE_TRANSACTION_TYPE";

    public CreateManageInvoiceTransactionTypeMessage(UUID id) {
        this.id = id;
    }
}
