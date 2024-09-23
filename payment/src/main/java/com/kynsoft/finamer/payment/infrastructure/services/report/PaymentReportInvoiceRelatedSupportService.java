package com.kynsoft.finamer.payment.infrastructure.services.report;

import com.kynsof.share.core.infrastructure.util.PDFUtils;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentContentProvider;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
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
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service(PaymentReportInvoiceRelatedSupportService.BEAN_ID)
public class PaymentReportInvoiceRelatedSupportService implements IPaymentReport {
    public static final String BEAN_ID = "INVOICE_RELATED_SUPPORT";
    private final IPaymentService paymentService;
    private final ReportContentProviderFactory reportContentProviderFactory;
    private final IPaymentDetailService paymentDetailService;

    public PaymentReportInvoiceRelatedSupportService(IPaymentService paymentService,
                                                     ReportContentProviderFactory reportContentProviderFactory,
                                                     IPaymentDetailService paymentDetailService) {
        this.paymentService = paymentService;
        this.reportContentProviderFactory = reportContentProviderFactory;
        this.paymentDetailService = paymentDetailService;
    }

    @Override
    public Optional<byte[]> generateReport(UUID paymentId) {
        try {
            PaymentDto paymentDto = paymentService.findById(paymentId);
            List<InputStream> contentToMerge = new LinkedList<>();
            this.getInvoiceRelate(paymentDto).forEach(invoiceId-> {
                Optional<byte[]> invoiceRelatedAttachment = this.getInvoiceRelatedAttachmentContent(invoiceId.toString());
                invoiceRelatedAttachment.ifPresent(content -> contentToMerge.add(new ByteArrayInputStream(content)));
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

    private List<UUID> getInvoiceRelate(PaymentDto paymentDto) {
        List<PaymentDetailDto> paymentDetailDtos =  paymentDetailService.findByPaymentId(paymentDto.getId());
        List<ManageBookingDto> manageBookingDtos =paymentDetailDtos.stream()
                .map(PaymentDetailDto::getManageBooking)
                .filter(Objects::nonNull).toList();
        List<ManageInvoiceDto> manageInvoiceDtos = manageBookingDtos.stream()
                .map(ManageBookingDto::getInvoice)
                .filter(Objects::nonNull).toList();
        return manageInvoiceDtos.stream().map(ManageInvoiceDto::getId).toList();
    }

}
