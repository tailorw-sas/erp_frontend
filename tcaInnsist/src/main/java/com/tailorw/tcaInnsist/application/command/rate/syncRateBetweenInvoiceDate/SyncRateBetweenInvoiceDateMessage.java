package com.tailorw.tcaInnsist.application.command.rate.syncRateBetweenInvoiceDate;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class SyncRateBetweenInvoiceDateMessage implements ICommandMessage {
    private final String command = "SYNC_RATE_BETWEEN_INVOICE_DATES";

    public SyncRateBetweenInvoiceDateMessage(){}
}
