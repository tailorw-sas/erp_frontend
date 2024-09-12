package com.kynsoft.finamer.invoicing.infrastructure.services.report;

import com.kynsoft.finamer.invoicing.application.query.report.InvoiceReportResponse;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceContentProvider;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceReport;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.infrastructure.services.report.content.AbstractReportContentProvider;
import com.kynsoft.finamer.invoicing.infrastructure.services.report.content.ReportContentProviderFactory;
import com.kynsoft.finamer.invoicing.infrastructure.services.report.util.ReportUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service(InvoiceReportInvoiceAndBookingService.BEAN_ID)
public class InvoiceReportInvoiceAndBookingService implements IInvoiceReport {
    public static final String BEAN_ID = "INVOICE_RELATED_SUPPORT";
    private final ReportContentProviderFactory reportContentProviderFactory;

    public InvoiceReportInvoiceAndBookingService(ReportContentProviderFactory reportContentProviderFactory) {
        this.reportContentProviderFactory = reportContentProviderFactory;
    }

    @Override
    public InvoiceReportResponse generateReport(UUID invoiceId) {
        try {
          Optional<byte[]> invoiceAndBookingContent = getInvoiceAndBooking(invoiceId);
          if (invoiceAndBookingContent.isPresent()){
              return ReportUtil.createPaymentReportResponse(invoiceAndBookingContent.get(),invoiceId+".pdf");
          }else{
              return ReportUtil.createPaymentReportResponse(ReportUtil.defaultPdfContent(),invoiceId+".pdf");
          }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private Optional<byte[]> getInvoiceAndBooking(UUID invoiceId) throws IOException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("invoiceId", invoiceId);
        AbstractReportContentProvider abstractReportContentProvider = reportContentProviderFactory.getReportContentProvider(EInvoiceContentProvider.INVOICE_AND_BOOKING_CONTENT);
        return abstractReportContentProvider.getContent(parameters);
    }


}
