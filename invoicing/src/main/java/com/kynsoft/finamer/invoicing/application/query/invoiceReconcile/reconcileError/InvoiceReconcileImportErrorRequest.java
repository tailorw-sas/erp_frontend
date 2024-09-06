package com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.reconcileError;

import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
public class InvoiceReconcileImportErrorRequest {
    private final String query;
    private final Pageable pageable;

    public InvoiceReconcileImportErrorRequest(String query, Pageable pageable) {

        this.query = query;
        this.pageable = pageable;
    }
}
