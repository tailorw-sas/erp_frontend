package com.kynsoft.finamer.invoicing.application.command.manageInvoiceStatus.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManageInvoiceStatusMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGE_INVOICE_STATUS";

    public DeleteManageInvoiceStatusMessage(UUID id) {
        this.id = id;
    }
}
