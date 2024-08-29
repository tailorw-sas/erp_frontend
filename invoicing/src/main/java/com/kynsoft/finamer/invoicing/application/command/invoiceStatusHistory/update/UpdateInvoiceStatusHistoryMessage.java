package com.kynsoft.finamer.invoicing.application.command.invoiceStatusHistory.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class UpdateInvoiceStatusHistoryMessage implements ICommandMessage {

    private final String command = "UPDATE_INVOICE_STATUS_HISTORY";

    public UpdateInvoiceStatusHistoryMessage() {
    }
}
