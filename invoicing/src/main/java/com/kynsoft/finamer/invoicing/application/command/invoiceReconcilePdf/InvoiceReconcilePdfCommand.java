package com.kynsoft.finamer.invoicing.application.command.invoiceReconcilePdf;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class InvoiceReconcilePdfCommand implements ICommand {

    private String[] ids;
    private byte[] pdfBytes;
    public InvoiceReconcilePdfCommand(String[] ids, byte[] pdfBytes) {
        this.ids = ids;
        this.pdfBytes = pdfBytes;
    }
    @Override
    public ICommandMessage getMessage() {
        return new InvoiceReconcilePdfMessage(ids, pdfBytes);
    }
}
