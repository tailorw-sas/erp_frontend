package com.kynsoft.finamer.invoicing.application.query.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InvoiceReportRequest {
    private String [] invoiceId;
    private String [] invoiceType;
}
