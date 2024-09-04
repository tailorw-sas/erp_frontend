package com.kynsoft.finamer.payment.infrastructure.services.report;

import com.kynsof.share.core.infrastructure.util.PDFUtils;
import com.kynsoft.finamer.payment.application.query.report.PaymentReportResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentContentProvider;
import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentReportType;
import com.kynsoft.finamer.payment.domain.services.IPaymentReport;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.infrastructure.services.report.content.AbstractReportContentProvider;
import com.kynsoft.finamer.payment.infrastructure.services.report.content.ReportContentProviderFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.kynsoft.finamer.payment.infrastructure.services.report.util.ReportUtil.createPaymentReportResponse;

@Service(PaymentReportPaymentSupportService.BEAN_ID)
public class PaymentReportPaymentSupportService implements IPaymentReport {
    public static final String BEAN_ID = "PAYMENT_SUPPORT";
    private final IPaymentService paymentService;
    private final ReportContentProviderFactory reportContentProviderFactory;

    public PaymentReportPaymentSupportService(IPaymentService paymentService,
                                              ReportContentProviderFactory reportContentProviderFactory) {
        this.paymentService = paymentService;
        this.reportContentProviderFactory = reportContentProviderFactory;
    }

    @Override
    public PaymentReportResponse generateReport(EPaymentReportType reportType, UUID paymentId) {
        try {
            PaymentDto paymentDto = paymentService.findById(paymentId);
            Optional<byte[]> paymentDetails = this.getPaymentDetailsReportContent(paymentDto.getId().toString());
            Optional<byte[]> paymentAllSupport = this.getPaymentSupportContent(paymentDto.getId().toString());
            List<InputStream> contentToMerge = new LinkedList<>();
            paymentDetails.ifPresent(content -> {
                contentToMerge.add(new ByteArrayInputStream(content));
            });
            paymentAllSupport.ifPresent(content -> {
                contentToMerge.add(new ByteArrayInputStream(content));
            });
            if (!contentToMerge.isEmpty()) {
                ByteArrayOutputStream result = PDFUtils.mergePDF(contentToMerge);
                return createPaymentReportResponse(result.toByteArray(), paymentId + ".pdf");
            } else {
                return createPaymentReportResponse(null, paymentId + ".pdf");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<byte[]> getPaymentDetailsReportContent(String paymentId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("paymentId", paymentId);
        AbstractReportContentProvider contentProvider = reportContentProviderFactory
                .getReportContentProvider(EPaymentContentProvider.PAYMENT_DETAILS_REPORT_CONTENT);
        return contentProvider.getContent(parameters);
    }

    private Optional<byte[]> getPaymentSupportContent(String paymentId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("paymentId", paymentId);
        AbstractReportContentProvider contentProvider = reportContentProviderFactory
                .getReportContentProvider(EPaymentContentProvider.PAYMENT_SUPPORT_CONTENT);
        return contentProvider.getContent(parameters);
    }


}
