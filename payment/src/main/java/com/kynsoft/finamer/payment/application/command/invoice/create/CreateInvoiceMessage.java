package com.kynsoft.finamer.payment.application.command.invoice.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class CreateInvoiceMessage implements ICommandMessage {

    private final String command = "CREATE_INVOICE";

    public CreateInvoiceMessage() {
    }

}
