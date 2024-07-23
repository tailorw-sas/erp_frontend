package com.kynsoft.finamer.invoicing.application.command.invoicecloseoperation.updateAll;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class UpdateAllInvoiceCloseOperationMessage implements ICommandMessage {

    private final String command = "UPDATE_ALL";

    public UpdateAllInvoiceCloseOperationMessage() {
    }

}
