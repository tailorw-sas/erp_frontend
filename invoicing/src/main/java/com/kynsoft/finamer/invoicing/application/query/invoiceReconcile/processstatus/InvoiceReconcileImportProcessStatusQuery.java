package com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

@Getter
public class InvoiceReconcileImportProcessStatusQuery implements IQuery {
    private final InvoiceReconcileImportProcessStatusRequest request;
    public InvoiceReconcileImportProcessStatusQuery(InvoiceReconcileImportProcessStatusRequest request1) {
        this.request = request1;
    }
}
