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
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

@Service(PaymentReportInvoiceRelatedService.BEAN_ID)
public class PaymentReportInvoiceRelatedService implements IPaymentReport {
    public static final String BEAN_ID = "INVOICE_RELATED";
    private final IPaymentService paymentService;

    private final IPaymentDetailService paymentDetailService;
    private final ReportContentProviderFactory reportContentProviderFactory;

    public PaymentReportInvoiceRelatedService(IPaymentService paymentService, IPaymentDetailService paymentDetailService,
                                              ReportContentProviderFactory reportContentProviderFactory) {
        this.paymentService = paymentService;
        this.paymentDetailService = paymentDetailService;
        this.reportContentProviderFactory = reportContentProviderFactory;
    }

    @Override
    public Optional<byte[]> generateReport(UUID paymentId) {
        try {
            PaymentDto paymentDto = paymentService.findById(paymentId);
            List<InputStream> contentToMerge = new LinkedList<>();
            this.getInvoiceRelate(paymentDto).forEach(invoiceId -> {
                Optional<byte[]> invoiceRelateReportContent = this.getInvoiceRelateReportContent(invoiceId.toString());
                invoiceRelateReportContent.ifPresent(content -> contentToMerge.add(new ByteArrayInputStream(content)));
            });
            if (!contentToMerge.isEmpty()) {
                return Optional.of(PDFUtils.mergePDFtoByte(contentToMerge));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();

    }

    private Optional<byte[]> getInvoiceRelateReportContent(String invoiceId) {
        AbstractReportContentProvider contentProvider = reportContentProviderFactory
                .getReportContentProvider(EPaymentContentProvider.INVOICE_REPORT_CONTENT);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("invoiceId", invoiceId);
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
