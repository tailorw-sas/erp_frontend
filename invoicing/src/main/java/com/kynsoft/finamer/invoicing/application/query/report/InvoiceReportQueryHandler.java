package com.kynsoft.finamer.invoicing.application.query.report;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceReportType;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceReport;
import com.kynsoft.finamer.invoicing.infrastructure.services.report.factory.InvoiceReportProviderFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class InvoiceReportQueryHandler implements IQueryHandler<InvoiceReportQuery, InvoiceReportResponse> {
    private final InvoiceReportProviderFactory invoiceReportProviderFactory;

    public InvoiceReportQueryHandler(InvoiceReportProviderFactory invoiceReportProviderFactory) {
        this.invoiceReportProviderFactory = invoiceReportProviderFactory;
    }

    @Override
    public InvoiceReportResponse handle(InvoiceReportQuery query) {
        InvoiceReportRequest invoiceReportRequest = query.getInvoiceReportRequest();
        IInvoiceReport invoiceReport = invoiceReportProviderFactory.
                getInvoiceReportService(EInvoiceReportType.valueOf(invoiceReportRequest.getInvoiceType()));
        try {
            return invoiceReport.generateReport(
                    UUID.fromString(invoiceReportRequest.getInvoiceId()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
