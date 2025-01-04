package com.kynsoft.finamer.invoicing.application.command.manageInvoice.update.calculateDueAmount;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateInvoiceCalculateDueAmountMessage implements ICommandMessage {

    private final UUID id;

    public UpdateInvoiceCalculateDueAmountMessage(UUID id) {
        this.id = id;
    }

}
