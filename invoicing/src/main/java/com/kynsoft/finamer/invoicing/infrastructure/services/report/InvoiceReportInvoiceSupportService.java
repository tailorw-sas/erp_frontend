package com.kynsoft.finamer.invoicing.infrastructure.services.report;

import com.kynsof.share.core.infrastructure.util.PDFUtils;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceContentProvider;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceReport;
import com.kynsoft.finamer.invoicing.infrastructure.services.report.content.AbstractReportContentProvider;
import com.kynsoft.finamer.invoicing.infrastructure.services.report.content.ReportContentProviderFactory;
import com.kynsoft.finamer.invoicing.infrastructure.services.report.util.ReportUtil;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service(InvoiceReportInvoiceSupportService.BEAN_ID)
public class InvoiceReportInvoiceSupportService implements IInvoiceReport {
    public static final String BEAN_ID = "INVOICE_SUPPORT";
    private final ReportContentProviderFactory reportContentProviderFactory;

    public InvoiceReportInvoiceSupportService(ReportContentProviderFactory reportContentProviderFactory) {
        this.reportContentProviderFactory = reportContentProviderFactory;
    }

    @Override
    public Optional<byte[]> generateReport(String invoiceId) {
        try {
            List<InputStream> contentToMerge = new LinkedList<>();
            Optional<byte[]> invoiceSupport = getInvoiceSupport(invoiceId);
            invoiceSupport.ifPresent(content -> contentToMerge.add(new ByteArrayInputStream(content)));
            if (!contentToMerge.isEmpty()) {
                return Optional.of(PDFUtils.mergePDFtoByte(contentToMerge));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public byte[] concatenatePDFs(String[] ids) {
        return new byte[0];
    }

    private Optional<byte[]> getInvoiceSupport(String invoiceId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("invoiceId", invoiceId);
        AbstractReportContentProvider abstractReportContentProvider = reportContentProviderFactory.
                getReportContentProvider(EInvoiceContentProvider.INVOICE_SUPPORT_CONTENT);
        return abstractReportContentProvider.getContent(parameters);
    }

}
