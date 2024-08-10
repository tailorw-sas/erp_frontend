package com.kynsoft.finamer.payment.domain.services;

import com.kynsoft.finamer.payment.application.query.report.PaymentReportResponse;
import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentReportType;

import java.io.IOException;
import java.util.UUID;

public interface IPaymentReport {

    PaymentReportResponse generateReport(EPaymentReportType reportType, UUID paymentId) throws IOException;
}
