package com.kynsoft.finamer.invoicing.application.command.manageInvoiceType.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageInvoiceTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_INVOICE_TYPE";

    public UpdateManageInvoiceTypeMessage(UUID id) {
        this.id = id;
    }
}
