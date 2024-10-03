package com.kynsoft.finamer.invoicing.application.command.manageInvoice.reconcileManual;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReconcileManualErrorResponse {
    private String invoiceId;
    private String message;
}
