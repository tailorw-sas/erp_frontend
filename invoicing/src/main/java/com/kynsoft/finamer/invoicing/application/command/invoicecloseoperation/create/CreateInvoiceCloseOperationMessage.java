package com.kynsoft.finamer.invoicing.application.command.invoicecloseoperation.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import java.util.UUID;
import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
public class CreateInvoiceCloseOperationMessage implements ICommandMessage {

    private UUID id;

    public CreateInvoiceCloseOperationMessage(UUID id) {
        this.id = id;
    }

}
