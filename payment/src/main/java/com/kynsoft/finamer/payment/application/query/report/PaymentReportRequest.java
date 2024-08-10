package com.kynsoft.finamer.payment.application.query.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentReportRequest {
    private String paymentId;
    private String paymentType;
}
