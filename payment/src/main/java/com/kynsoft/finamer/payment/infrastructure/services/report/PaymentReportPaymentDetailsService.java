package com.kynsoft.finamer.payment.infrastructure.services.report;

import com.kynsoft.finamer.payment.application.query.report.PaymentReportResponse;
import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentContentProvider;
import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentReportType;
import com.kynsoft.finamer.payment.domain.services.IPaymentReport;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.infrastructure.services.report.content.AbstractReportContentProvider;
import com.kynsoft.finamer.payment.infrastructure.services.report.content.ReportContentProviderFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.kynsoft.finamer.payment.infrastructure.services.report.util.ReportUtil.createPaymentReportResponse;

@Service(PaymentReportPaymentDetailsService.BEAN_ID)
public class PaymentReportPaymentDetailsService implements IPaymentReport {
    public static final String BEAN_ID = "PAYMENT_DETAILS";
    private final ReportContentProviderFactory reportContentProviderFactory;

    public PaymentReportPaymentDetailsService(ReportContentProviderFactory reportContentProviderFactory) {
        this.reportContentProviderFactory = reportContentProviderFactory;

    }

    @Override
    public PaymentReportResponse generateReport(EPaymentReportType reportType, UUID paymentId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("paymentId", paymentId);
        AbstractReportContentProvider contentProvider = reportContentProviderFactory.getReportContentProvider(EPaymentContentProvider.PAYMENT_DETAILS_REPORT_CONTENT);
        Optional<byte[]> pdfContent = contentProvider.getContent(parameters);
        try {
            if (pdfContent.isPresent()) {
                return createPaymentReportResponse(pdfContent.get(), paymentId.toString() + ".pdf");
            }else{
                return createPaymentReportResponse(null,"default.pdf");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
