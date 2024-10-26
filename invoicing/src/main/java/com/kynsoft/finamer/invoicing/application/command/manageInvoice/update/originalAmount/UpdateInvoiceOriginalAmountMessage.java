package com.kynsoft.finamer.invoicing.application.command.manageInvoice.update.originalAmount;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateInvoiceOriginalAmountMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_INVOICE";

    public UpdateInvoiceOriginalAmountMessage(UUID id) {
        this.id = id;
    }

}
