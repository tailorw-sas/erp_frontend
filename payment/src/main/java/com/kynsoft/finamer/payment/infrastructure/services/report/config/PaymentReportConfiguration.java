package com.kynsoft.finamer.payment.infrastructure.services.report.config;

import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentReportType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentReportConfiguration {

    @Value("${report.code.payment.details:aaa}")
    private String paymentDetailsReportCode;

    @Value("${report.code.invoice.booking:inv}")
    private String invoiceReportCode;

    // Orden fijo para garantizar consistencia
    private static final List<EPaymentReportType> REPORT_ORDER = List.of(
            EPaymentReportType.PAYMENT_DETAILS,
            EPaymentReportType.INVOICE_RELATED,
            EPaymentReportType.INVOICE_RELATED_SUPPORT,
            EPaymentReportType.PAYMENT_SUPPORT,
            EPaymentReportType.ALL_SUPPORT
    );

    public String getReportCode(EPaymentReportType reportType) {
        return switch (reportType) {
            case PAYMENT_DETAILS, PAYMENT_SUPPORT, ALL_SUPPORT -> paymentDetailsReportCode;
            case INVOICE_RELATED, INVOICE_RELATED_SUPPORT -> invoiceReportCode;
        };
    }

    public List<EPaymentReportType> getReportOrder() {
        return REPORT_ORDER;
    }
}
