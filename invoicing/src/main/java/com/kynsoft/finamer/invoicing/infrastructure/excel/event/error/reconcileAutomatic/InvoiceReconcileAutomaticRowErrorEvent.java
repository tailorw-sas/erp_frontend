package com.kynsoft.finamer.invoicing.infrastructure.excel.event.error.reconcileAutomatic;

import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.automatic.InvoiceReconcileAutomaticImportErrorEntity;
import lombok.Data;
import lombok.Getter;

@Data
public class InvoiceReconcileAutomaticRowErrorEvent {
    private InvoiceReconcileAutomaticImportErrorEntity error;
}
