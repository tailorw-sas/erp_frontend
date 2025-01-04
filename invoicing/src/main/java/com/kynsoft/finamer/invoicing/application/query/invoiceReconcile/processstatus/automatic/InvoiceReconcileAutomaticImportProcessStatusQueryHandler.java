package com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus.automatic;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceReconcileAutomaticService;
import com.kynsoft.finamer.invoicing.domain.services.InvoiceReconcileImportService;
import org.springframework.stereotype.Component;

@Component
public class InvoiceReconcileAutomaticImportProcessStatusQueryHandler implements IQueryHandler<InvoiceReconcileAutomaticImportProcessStatusQuery,
        InvoiceReconcileAutomaticImportProcessStatusResponse> {

    private final IInvoiceReconcileAutomaticService reconcileImportService;

    public InvoiceReconcileAutomaticImportProcessStatusQueryHandler(IInvoiceReconcileAutomaticService reconcileImportService) {
        this.reconcileImportService = reconcileImportService;
    }

    @Override
    public InvoiceReconcileAutomaticImportProcessStatusResponse handle(InvoiceReconcileAutomaticImportProcessStatusQuery query) {
            return reconcileImportService.getImportProcessStatus(query.getRequest());
    }
}
