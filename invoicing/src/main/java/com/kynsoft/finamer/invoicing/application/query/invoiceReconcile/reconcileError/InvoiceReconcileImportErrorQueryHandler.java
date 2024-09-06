package com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.reconcileError;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.InvoiceReconcileImportService;
import org.springframework.stereotype.Component;

@Component
public class InvoiceReconcileImportErrorQueryHandler implements IQueryHandler<InvoiceReconcileImportErrorQuery, PaginatedResponse> {

    private final InvoiceReconcileImportService invoiceReconcileImportService;

    public InvoiceReconcileImportErrorQueryHandler(InvoiceReconcileImportService invoiceReconcileImportService) {
        this.invoiceReconcileImportService = invoiceReconcileImportService;
    }

    @Override
    public PaginatedResponse handle(InvoiceReconcileImportErrorQuery query) {
        return invoiceReconcileImportService.getReconcileImportErrors(query.getRequest());
    }
}
