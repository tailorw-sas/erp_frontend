package com.kynsoft.finamer.invoicing.application.command.manageInvoiceStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageInvoiceStatusMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_INVOICE_STATUS";

    public CreateManageInvoiceStatusMessage(UUID id) {
        this.id = id;
    }
}
