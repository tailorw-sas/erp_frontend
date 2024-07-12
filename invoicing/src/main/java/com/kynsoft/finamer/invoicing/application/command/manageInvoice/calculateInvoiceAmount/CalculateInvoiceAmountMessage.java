package com.kynsoft.finamer.invoicing.application.command.manageInvoice.calculateInvoiceAmount;

import java.util.UUID;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;

import lombok.Getter;

@Getter
public class CalculateInvoiceAmountMessage implements ICommandMessage {
    private UUID id;
    private String message = "CALCULATE_INVOICE_AMOUNT";

    public CalculateInvoiceAmountMessage(UUID id) {
        this.id = id;
    }

}
