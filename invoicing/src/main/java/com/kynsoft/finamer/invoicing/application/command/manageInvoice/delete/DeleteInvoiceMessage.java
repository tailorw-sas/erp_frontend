package com.kynsoft.finamer.invoicing.application.command.manageInvoice.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteInvoiceMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_INVOICE";

    public DeleteInvoiceMessage(UUID id) {
        this.id = id;
    }

}
