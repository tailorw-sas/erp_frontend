package com.kynsoft.finamer.invoicing.application.command.invoiceReconcileManualPdf;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class InvoiceReconcileManualPdfRequest {

    private List<UUID> invoices;
    private byte[] pdfData;
}
