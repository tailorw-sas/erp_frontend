package com.kynsoft.finamer.invoicing.application.command.manageInvoice.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateInvoiceMessage implements ICommandMessage {

    private final UUID id;
    private final Long invoiceId;

    private final String command = "CREATE_INVOICE";

    public CreateInvoiceMessage(UUID id, Long invoiceId) {
        this.id = id;
        this.invoiceId = invoiceId;
    }

}
