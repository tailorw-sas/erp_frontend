package com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.Getter;

@Getter
public class InvoiceReconcileImportProcessStatusResponse implements IResponse {
    private final String status;
    public InvoiceReconcileImportProcessStatusResponse(String status) {
        this.status = status;
    }
}
