package com.kynsoft.finamer.invoicing.application.command.manageInvoice.update.calculateInvoiceAmount;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateInvoiceCalculateInvoiceAmountMessage implements ICommandMessage {

    private final UUID id;

    public UpdateInvoiceCalculateInvoiceAmountMessage(UUID id) {
        this.id = id;
    }

}
