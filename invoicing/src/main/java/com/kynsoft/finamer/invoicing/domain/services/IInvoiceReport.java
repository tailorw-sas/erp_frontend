package com.kynsoft.finamer.invoicing.domain.services;



import com.kynsoft.finamer.invoicing.application.query.report.InvoiceReportResponse;

import java.io.IOException;
import java.util.UUID;

public interface IInvoiceReport {

    InvoiceReportResponse generateReport(UUID paymentId) throws IOException;
}
