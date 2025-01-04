package com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus;

import lombok.Getter;

@Getter
public class InvoiceReconcileImportProcessStatusRequest {

    private final String importProcessId;

    public InvoiceReconcileImportProcessStatusRequest(String importProcessId1) {

        this.importProcessId = importProcessId1;
    }
}
