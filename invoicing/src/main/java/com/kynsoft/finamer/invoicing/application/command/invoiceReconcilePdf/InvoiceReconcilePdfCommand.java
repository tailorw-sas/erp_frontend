package com.kynsoft.finamer.invoicing.application.command.invoiceReconcilePdf;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class InvoiceReconcilePdfCommand implements ICommand {

    private UUID id;
    private byte[] pdfData;
    public InvoiceReconcilePdfCommand(UUID id, byte[] pdfData) {
        this.id = id;
        this.pdfData = pdfData;
    }
    @Override
    public ICommandMessage getMessage() {
        return new InvoiceReconcilePdfMessage(id, pdfData);
    }
}
