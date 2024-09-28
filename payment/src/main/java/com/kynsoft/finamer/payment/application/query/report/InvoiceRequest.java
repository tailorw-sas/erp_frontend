package com.kynsoft.finamer.payment.application.query.report;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class InvoiceRequest {
    private String [] invoiceId;
    private String [] invoiceType;
}
