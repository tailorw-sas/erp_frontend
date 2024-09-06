package com.kynsoft.finamer.invoicing.domain.event.importError;

import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.InvoiceReconcileImportError;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CreateImportErrorEvent extends ApplicationEvent {
    private final InvoiceReconcileImportError reconcileImportError;
    public CreateImportErrorEvent(Object source, InvoiceReconcileImportError reconcileImportError) {
        super(source);
        this.reconcileImportError = reconcileImportError;
    }
}
