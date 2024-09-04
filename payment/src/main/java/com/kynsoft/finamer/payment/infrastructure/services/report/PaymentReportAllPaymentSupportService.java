package com.kynsoft.finamer.payment.infrastructure.services.report;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
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
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.kynsoft.finamer.payment.infrastructure.services.report.util.ReportUtil.createPaymentReportResponse;

@Service(value = PaymentReportAllPaymentSupportService.BEAN_ID)
public class PaymentReportAllPaymentSupportService implements IPaymentReport {
    public static final String BEAN_ID = "ALL_SUPPORT";
    private final ReportContentProviderFactory reportContentProviderFactory;
    private final IPaymentService paymentService;

    public PaymentReportAllPaymentSupportService(ReportContentProviderFactory reportContentProviderFactory,
                                                 IPaymentService paymentService) {
        this.reportContentProviderFactory = reportContentProviderFactory;
        this.paymentService = paymentService;
    }

    @Override
    public PaymentReportResponse generateReport(EPaymentReportType reportType, UUID paymentId) {
        try {
            PaymentDto paymentDto = paymentService.findById(paymentId);
            Optional<byte[]> paymentDetails = this.getPaymentDetailsReportContent(paymentDto.getId().toString());
            Optional<byte[]> paymentAllSupport = this.getPaymentAllSupportContent(paymentDto.getId().toString());
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

    private Optional<byte[]> getPaymentAllSupportContent(String paymentId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("paymentId", paymentId);
        AbstractReportContentProvider contentProvider = reportContentProviderFactory
                .getReportContentProvider(EPaymentContentProvider.PAYMENT_ALL_SUPPORT_CONTENT);
        return contentProvider.getContent(parameters);
    }

    private PaymentReportResponse createPaymentReportResponse(byte[] pdfContent, String fileName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(pdfContent);
        return new PaymentReportResponse(fileName, baos);
    }
}
