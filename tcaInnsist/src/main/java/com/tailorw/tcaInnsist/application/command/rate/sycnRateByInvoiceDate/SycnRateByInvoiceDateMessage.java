package com.tailorw.tcaInnsist.application.command.rate.sycnRateByInvoiceDate;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class SycnRateByInvoiceDateMessage implements ICommandMessage {

    private final String command = "SYNC_RATE_BY_INVOICE_DATE_COMMAND";

    public SycnRateByInvoiceDateMessage(){

    }
}
