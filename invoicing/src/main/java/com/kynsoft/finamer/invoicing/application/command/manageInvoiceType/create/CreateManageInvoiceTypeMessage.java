package com.kynsoft.finamer.invoicing.application.command.manageInvoiceType.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageInvoiceTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_INVOICE_TYPE";

    public CreateManageInvoiceTypeMessage(UUID id) {
        this.id = id;
    }
}
