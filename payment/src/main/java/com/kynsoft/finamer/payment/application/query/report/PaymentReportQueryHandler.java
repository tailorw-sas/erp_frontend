package com.kynsoft.finamer.payment.application.query.report;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentReportType;
import com.kynsoft.finamer.payment.domain.services.IPaymentReport;
import com.kynsoft.finamer.payment.infrastructure.services.factory.PaymentReportProviderFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class PaymentReportQueryHandler implements IQueryHandler<PaymentReportQuery, PaymentReportResponse> {
    private final PaymentReportProviderFactory paymentReportProviderFactory;

    public PaymentReportQueryHandler(PaymentReportProviderFactory paymentReportProviderFactory) {
        this.paymentReportProviderFactory = paymentReportProviderFactory;
    }

    @Override
    public PaymentReportResponse handle(PaymentReportQuery query) {
        PaymentReportRequest paymentReportRequest = query.getPaymentReportRequest();
        IPaymentReport paymentReport = paymentReportProviderFactory.
                getPaymentReportService(EPaymentReportType.valueOf(paymentReportRequest.getPaymentType()));
        try {
            return paymentReport.generateReport(EPaymentReportType.valueOf(paymentReportRequest.getPaymentType()),
                    UUID.fromString(paymentReportRequest.getPaymentId()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
