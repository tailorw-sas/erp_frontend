package com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.reconcileError;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

@Getter
public class InvoiceReconcileImportErrorQuery implements IQuery {

    private final InvoiceReconcileImportErrorRequest request;
    public InvoiceReconcileImportErrorQuery(InvoiceReconcileImportErrorRequest request1) {

        this.request = request1;
    }
}
