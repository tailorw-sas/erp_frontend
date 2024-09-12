package com.kynsoft.finamer.invoicing.infrastructure.services.report;

import com.kynsof.share.core.infrastructure.util.PDFUtils;
import com.kynsoft.finamer.invoicing.application.query.report.InvoiceReportResponse;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceContentProvider;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceReport;
import com.kynsoft.finamer.invoicing.infrastructure.services.report.content.AbstractReportContentProvider;
import com.kynsoft.finamer.invoicing.infrastructure.services.report.content.ReportContentProviderFactory;
import com.kynsoft.finamer.invoicing.infrastructure.services.report.util.ReportUtil;
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

@Service(InvoiceReportInvoiceSupportService.BEAN_ID)
public class InvoiceReportInvoiceSupportService implements IInvoiceReport {
    public static final String BEAN_ID = "INVOICE_RELATED";
    private final ReportContentProviderFactory reportContentProviderFactory;

    public InvoiceReportInvoiceSupportService(ReportContentProviderFactory reportContentProviderFactory) {
        this.reportContentProviderFactory = reportContentProviderFactory;
    }

    @Override
    public InvoiceReportResponse generateReport(UUID invoiceId) {
        try {
            List<InputStream> contentToMerge = new LinkedList<>();
            Optional<byte[]> invoiceAndBooking = getInvoiceAndBooking(invoiceId);
            Optional<byte[]> invoiceSupport = getInvoiceSupport(invoiceId);
            invoiceAndBooking.ifPresent(content->contentToMerge.add(new ByteArrayInputStream(content)));
            invoiceSupport.ifPresent(content->contentToMerge.add(new ByteArrayInputStream(content)));
            if (!contentToMerge.isEmpty()) {
                ByteArrayOutputStream result = PDFUtils.mergePDF(contentToMerge);
                return ReportUtil.createPaymentReportResponse(result.toByteArray(), invoiceId + ".pdf");
            }else{
                return ReportUtil.createPaymentReportResponse(ReportUtil.defaultPdfContent(),invoiceId+".pdf");
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private Optional<byte[]> getInvoiceAndBooking(UUID invoiceId) throws IOException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("invoiceId", invoiceId);
        AbstractReportContentProvider abstractReportContentProvider = reportContentProviderFactory.getReportContentProvider(EInvoiceContentProvider.INVOICE_AND_BOOKING_CONTENT);
        return abstractReportContentProvider.getContent(parameters);
    }
    private Optional<byte[]> getInvoiceSupport(UUID invoiceId){
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("invoiceId", invoiceId);
        AbstractReportContentProvider abstractReportContentProvider = reportContentProviderFactory.
                getReportContentProvider(EInvoiceContentProvider.INVOICE_SUPPORT_CONTENT);
        return abstractReportContentProvider.getContent(parameters);
    }

}
