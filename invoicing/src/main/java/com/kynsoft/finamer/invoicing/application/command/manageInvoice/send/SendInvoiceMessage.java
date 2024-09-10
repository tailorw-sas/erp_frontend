package com.kynsoft.finamer.invoicing.application.command.manageInvoice.send;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SendInvoiceMessage implements ICommandMessage {

    private final String command = "PARTIAL_CLONE_INVOICE";
    private boolean result;

   

    public SendInvoiceMessage( boolean result) {

        this.result = result;
    }

}
