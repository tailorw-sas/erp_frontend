package com.kynsoft.finamer.invoicing.application.command.invoiceReconcileImport.importReconcile;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class InvoiceReconcileImportCommand implements ICommand {
    private final InvoiceReconcileImportRequest invoiceReconcileImportRequest;

    public InvoiceReconcileImportCommand(InvoiceReconcileImportRequest invoiceReconcileImportRequest) {
        this.invoiceReconcileImportRequest = invoiceReconcileImportRequest;
    }


    @Override
    public ICommandMessage getMessage() {
        return null;
    }
}
