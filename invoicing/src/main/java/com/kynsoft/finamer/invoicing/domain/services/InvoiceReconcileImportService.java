package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.application.command.invoiceReconcileImport.importReconcile.InvoiceReconcileImportRequest;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus.InvoiceReconcileImportProcessStatusRequest;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus.InvoiceReconcileImportProcessStatusResponse;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.reconcileError.InvoiceReconcileImportErrorRequest;

public interface InvoiceReconcileImportService {

    void importReconcileFromFile(InvoiceReconcileImportRequest request);
    PaginatedResponse getReconcileImportErrors(InvoiceReconcileImportErrorRequest request);

    InvoiceReconcileImportProcessStatusResponse getReconcileImportProcessStatus(InvoiceReconcileImportProcessStatusRequest request);
}
