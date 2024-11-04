package com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.reconcileError.automatic;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

@Getter
public class InvoiceReconcileAutomaticImportErrorQuery implements IQuery {

    private final InvoiceReconcileAutomaticImportErrorRequest request;
    public InvoiceReconcileAutomaticImportErrorQuery(InvoiceReconcileAutomaticImportErrorRequest request1) {

        this.request = request1;
    }
}
