package com.kynsoft.finamer.invoicing.application.command.invoiceReconcileImport.importReconcile;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.services.InvoiceReconcileImportService;
import org.springframework.stereotype.Component;

@Component
public class InvoiceReconcileImportCommandHandler implements ICommandHandler<InvoiceReconcileImportCommand> {
  private final InvoiceReconcileImportService reconcileImportService;

    public InvoiceReconcileImportCommandHandler(InvoiceReconcileImportService reconcileImportService) {
        this.reconcileImportService = reconcileImportService;
    }

    @Override
    public void handle(InvoiceReconcileImportCommand command) {
        reconcileImportService.importReconcileFromFile(command.getInvoiceReconcileImportRequest());
    }


}
