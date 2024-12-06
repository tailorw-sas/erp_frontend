package com.kynsoft.finamer.invoicing.application.command.invoiceReconcileManualPdf;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceReconcileManualPdfCommand implements ICommand{
    private final InvoiceReconcileManualPdfRequest request;

    public InvoiceReconcileManualPdfCommand(InvoiceReconcileManualPdfRequest request) {
        this.request = request;
    }

    @Override
    public ICommandMessage getMessage() {
        return new InvoiceReconcileManualPdfMessage(request.getPdfData());
    }
}

