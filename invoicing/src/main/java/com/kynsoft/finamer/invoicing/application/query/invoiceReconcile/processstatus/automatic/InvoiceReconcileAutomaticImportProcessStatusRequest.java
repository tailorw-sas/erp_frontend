package com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus.automatic;

import lombok.Getter;

@Getter
public class InvoiceReconcileAutomaticImportProcessStatusRequest {

    private final String importProcessId;

    public InvoiceReconcileAutomaticImportProcessStatusRequest(String importProcessId1) {

        this.importProcessId = importProcessId1;
    }
}
