package com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus.automatic;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.Getter;

@Getter
public class InvoiceReconcileAutomaticImportProcessStatusResponse implements IResponse {
    private final String status;
    public InvoiceReconcileAutomaticImportProcessStatusResponse(String status) {
        this.status = status;
    }
}
