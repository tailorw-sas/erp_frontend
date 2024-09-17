package com.kynsoft.finamer.payment.infrastructure.services.report;

import com.kynsof.share.core.infrastructure.util.PDFUtils;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentContentProvider;
import com.kynsoft.finamer.payment.domain.services.IPaymentReport;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.infrastructure.services.report.content.AbstractReportContentProvider;
import com.kynsoft.finamer.payment.infrastructure.services.report.content.ReportContentProviderFactory;
import com.kynsoft.finamer.payment.infrastructure.services.report.util.ReportUtil;
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

@Service(PaymentReportInvoiceRelatedSupportService.BEAN_ID)
public class PaymentReportInvoiceRelatedSupportService implements IPaymentReport {
    public static final String BEAN_ID = "INVOICE_RELATED_SUPPORT";
    private final IPaymentService paymentService;
    private final ReportContentProviderFactory reportContentProviderFactory;

    public PaymentReportInvoiceRelatedSupportService(IPaymentService paymentService,
                                                     ReportContentProviderFactory reportContentProviderFactory) {
        this.paymentService = paymentService;
        this.reportContentProviderFactory = reportContentProviderFactory;
    }

    @Override
    public Optional<byte[]> generateReport(UUID paymentId) {
        try {
            PaymentDto paymentDto = paymentService.findById(paymentId);
            Optional<ManageInvoiceDto> invoice = Optional.ofNullable(paymentDto.getInvoice());
            List<InputStream> contentToMerge = new LinkedList<>();
            invoice.ifPresent(manageInvoiceDto -> {
                Optional<byte[]> invoiceRelatedAttachment=this.getInvoiceRelatedAttachmentContent(manageInvoiceDto.getId().toString());
                invoiceRelatedAttachment.ifPresent(content->contentToMerge.add(new ByteArrayInputStream(content)));
            });
            if (!contentToMerge.isEmpty()) {
                return Optional.of(PDFUtils.mergePDFtoByte(contentToMerge));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }


    private Optional<byte[]> getInvoiceRelatedAttachmentContent(String invoiceId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("invoiceId", invoiceId);
        AbstractReportContentProvider contentProvider = reportContentProviderFactory
                .getReportContentProvider(EPaymentContentProvider.INVOICE_ATTACHMENT_CONTENT);
        return contentProvider.getContent(parameters);
    }

}
