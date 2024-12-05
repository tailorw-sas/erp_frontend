package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsoft.finamer.invoicing.application.command.invoiceReconcileManualPdf.InvoiceReconcileManualPdfRequest;
import java.io.IOException;

public interface IReportPdfService {
    byte[] concatenatePDFs(String[] ids) throws IOException;
    byte[] concatenateManualPDFs(InvoiceReconcileManualPdfRequest request) throws IOException;
}
