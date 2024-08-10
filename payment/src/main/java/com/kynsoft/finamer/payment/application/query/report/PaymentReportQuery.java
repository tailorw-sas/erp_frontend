package com.kynsoft.finamer.payment.application.query.report;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentReportQuery implements IQuery {
    private PaymentReportRequest paymentReportRequest;
}
