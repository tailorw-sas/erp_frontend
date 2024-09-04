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
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentContentProvider;
import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentReportType;
import com.kynsoft.finamer.payment.domain.services.IPaymentReport;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.infrastructure.services.report.content.AbstractReportContentProvider;
import com.kynsoft.finamer.payment.infrastructure.services.report.content.ReportContentProviderFactory;
import com.kynsoft.finamer.payment.infrastructure.services.report.util.ReportUtil;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service(PaymentReportInvoiceRelatedService.BEAN_ID)
public class PaymentReportInvoiceRelatedService implements IPaymentReport {
    public static final String BEAN_ID = "INVOICE_RELATED";
    private final IPaymentService paymentService;
    private final ReportContentProviderFactory reportContentProviderFactory;

    public PaymentReportInvoiceRelatedService(IPaymentService paymentService,
                                              ReportContentProviderFactory reportContentProviderFactory) {
        this.paymentService = paymentService;
        this.reportContentProviderFactory = reportContentProviderFactory;
    }

    @Override
    public PaymentReportResponse generateReport(EPaymentReportType reportType, UUID paymentId) {
        try {
            PaymentDto paymentDto = paymentService.findById(paymentId);
            Optional<byte[]> paymentDetailContent = this.getPaymentDetailsReportContent(paymentDto.getPaymentId());
            Optional<ManageInvoiceDto> invoice = Optional.ofNullable(paymentDto.getInvoice());
            List<InputStream> contentToMerge = new LinkedList<>();
            paymentDetailContent.ifPresent(content -> contentToMerge.add(new ByteArrayInputStream(content)));
            invoice.ifPresent(manageInvoiceDto -> {
                Optional<byte[]> invoiceRelateReportContent = this.getInvoiceRelateReportContent(manageInvoiceDto.getId().toString());
                invoiceRelateReportContent.ifPresent(content -> contentToMerge.add(new ByteArrayInputStream(content)));
            });
            if (!contentToMerge.isEmpty()) {
                ByteArrayOutputStream result = PDFUtils.mergePDF(contentToMerge);
                return ReportUtil.createPaymentReportResponse(result.toByteArray(), paymentId + ".pdf");
            } else {
                return ReportUtil.createPaymentReportResponse(null, paymentId + ".pdf");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private Optional<byte[]> getPaymentDetailsReportContent(long paymentId) {
        AbstractReportContentProvider contentProvider = reportContentProviderFactory
                .getReportContentProvider(EPaymentContentProvider.PAYMENT_DETAILS_REPORT_CONTENT);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("paymentId", paymentId);
        return contentProvider.getContent(parameters);
    }

    private Optional<byte[]> getInvoiceRelateReportContent(String invoiceId) {
        AbstractReportContentProvider contentProvider = reportContentProviderFactory
                .getReportContentProvider(EPaymentContentProvider.INVOICE_REPORT_CONTENT);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("invoiceRelatedId", invoiceId);
        return contentProvider.getContent(parameters);
    }

}
