package com.kynsoft.finamer.invoicing.application.command.invoicecloseoperation.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteInvoiceCloseOperationMessage implements ICommandMessage {

    private final UUID id;

    public DeleteInvoiceCloseOperationMessage(UUID id) {
        this.id = id;
    }

}
