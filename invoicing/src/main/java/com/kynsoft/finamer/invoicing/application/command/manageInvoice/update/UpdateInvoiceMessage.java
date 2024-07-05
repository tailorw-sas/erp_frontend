package com.kynsoft.finamer.invoicing.application.command.manageInvoice.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateInvoiceMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_INVOICE";

    public UpdateInvoiceMessage(UUID id) {
        this.id = id;
    }

}
