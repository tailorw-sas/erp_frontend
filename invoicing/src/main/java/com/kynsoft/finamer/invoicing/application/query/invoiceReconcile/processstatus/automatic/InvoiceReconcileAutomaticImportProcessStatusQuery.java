package com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus.automatic;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

@Getter
public class InvoiceReconcileAutomaticImportProcessStatusQuery implements IQuery {
    private final InvoiceReconcileAutomaticImportProcessStatusRequest request;
    public InvoiceReconcileAutomaticImportProcessStatusQuery(InvoiceReconcileAutomaticImportProcessStatusRequest request1) {
        this.request = request1;
    }
}
