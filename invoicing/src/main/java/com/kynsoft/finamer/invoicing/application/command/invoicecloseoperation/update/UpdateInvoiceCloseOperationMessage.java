package com.kynsoft.finamer.invoicing.application.command.invoicecloseoperation.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateInvoiceCloseOperationMessage implements ICommandMessage {

    private final UUID id;

    public UpdateInvoiceCloseOperationMessage(UUID id) {
        this.id = id;
    }

}
