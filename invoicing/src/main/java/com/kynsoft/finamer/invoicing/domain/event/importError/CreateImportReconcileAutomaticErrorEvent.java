package com.kynsoft.finamer.invoicing.domain.event.importError;

import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.InvoiceReconcileImportError;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.automatic.InvoiceReconcileAutomaticImportErrorEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CreateImportReconcileAutomaticErrorEvent extends ApplicationEvent {
    private final InvoiceReconcileAutomaticImportErrorEntity reconcileImportError;
    public CreateImportReconcileAutomaticErrorEvent(Object source, InvoiceReconcileAutomaticImportErrorEntity reconcileImportError) {
        super(source);
        this.reconcileImportError = reconcileImportError;
    }
}
