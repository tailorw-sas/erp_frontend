package com.kynsoft.finamer.invoicing.application.command.invoiceStatusHistory.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class CreateInvoiceStatusHistoryMessage implements ICommandMessage {

    private final String command = "CREATE_INVOICE_STATUS_HISTORY";

    public CreateInvoiceStatusHistoryMessage() {
    }
}
