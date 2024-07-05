package com.kynsoft.finamer.invoicing.application.command.manageInvoiceType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManageInvoiceTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGE_INVOICE_TYPE";

    public DeleteManageInvoiceTypeMessage(UUID id) {
        this.id = id;
    }
}
