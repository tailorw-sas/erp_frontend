package com.kynsoft.finamer.payment.infrastructure.services.report;

import com.kynsof.share.core.infrastructure.util.PDFUtils;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentContentProvider;
import com.kynsoft.finamer.payment.domain.services.IPaymentReport;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.infrastructure.services.report.content.AbstractReportContentProvider;
import com.kynsoft.finamer.payment.infrastructure.services.report.content.ReportContentProviderFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

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
    public Optional<byte[]> generateReport(UUID paymentId) {
        try {
            PaymentDto paymentDto = paymentService.findById(paymentId);
            Optional<byte[]> paymentAllSupport = this.getPaymentSupportContent(paymentDto.getId().toString());
            List<InputStream> contentToMerge = new LinkedList<>();
            paymentAllSupport.ifPresent(content -> {
                contentToMerge.add(new ByteArrayInputStream(content));
            });
            if (!contentToMerge.isEmpty()) {
                return Optional.of(PDFUtils.mergePDFtoByte(contentToMerge));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }


    private Optional<byte[]> getPaymentSupportContent(String paymentId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("paymentId", paymentId);
        AbstractReportContentProvider contentProvider = reportContentProviderFactory
                .getReportContentProvider(EPaymentContentProvider.PAYMENT_SUPPORT_CONTENT);
        return contentProvider.getContent(parameters);
    }


}
