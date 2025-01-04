package com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.reconcileError.automatic;

import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
public class InvoiceReconcileAutomaticImportErrorRequest {
    private final String query;
    private final Pageable pageable;

    public InvoiceReconcileAutomaticImportErrorRequest(String query, Pageable pageable) {

        this.query = query;
        this.pageable = pageable;
    }
}
