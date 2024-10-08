package com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.reconcileError.automatic;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceReconcileAutomaticService;
import com.kynsoft.finamer.invoicing.domain.services.InvoiceReconcileImportService;
import org.springframework.stereotype.Component;

@Component
public class InvoiceReconcileAutomaticImportErrorQueryHandler implements IQueryHandler<InvoiceReconcileAutomaticImportErrorQuery, PaginatedResponse> {

    private final IInvoiceReconcileAutomaticService invoiceReconcileImportService;

    public InvoiceReconcileAutomaticImportErrorQueryHandler(IInvoiceReconcileAutomaticService invoiceReconcileImportService) {
        this.invoiceReconcileImportService = invoiceReconcileImportService;
    }


    @Override
    public PaginatedResponse handle(InvoiceReconcileAutomaticImportErrorQuery query) {
        return invoiceReconcileImportService.getImportErrors(query.getRequest());
    }
}
