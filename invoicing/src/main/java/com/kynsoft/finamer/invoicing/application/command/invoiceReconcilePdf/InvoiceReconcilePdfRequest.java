package com.kynsoft.finamer.invoicing.application.command.invoiceReconcilePdf;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvoiceReconcilePdfRequest {

    private String[] invoiceIds;
    private byte[] pdfData;
}
