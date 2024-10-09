package com.kynsoft.finamer.invoicing.infrastructure.services.report;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceContentProvider;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceReport;
import com.kynsoft.finamer.invoicing.infrastructure.services.report.content.AbstractReportContentProvider;
import com.kynsoft.finamer.invoicing.infrastructure.services.report.content.ReportContentProviderFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service(InvoiceReportReconcileAutomaticService.BEAN_ID)
public class InvoiceReportReconcileAutomaticService implements IInvoiceReport {
    public static final String BEAN_ID = "RECONCILE_AUTO";
    private final ReportContentProviderFactory reportContentProviderFactory;

    public InvoiceReportReconcileAutomaticService(ReportContentProviderFactory reportContentProviderFactory) {
        this.reportContentProviderFactory = reportContentProviderFactory;
    }

    @Override
    public Optional<byte[]> generateReport(String invoiceId) {
        try {
            return getInvoiceAndBooking(invoiceId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<byte[]> getInvoiceAndBooking(String invoiceId) throws IOException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("invoiceId", invoiceId);
        AbstractReportContentProvider abstractReportContentProvider = reportContentProviderFactory.getReportContentProvider(EInvoiceContentProvider.RECONCILE_AUTO);
        return abstractReportContentProvider.getContent(parameters);
    }


}
