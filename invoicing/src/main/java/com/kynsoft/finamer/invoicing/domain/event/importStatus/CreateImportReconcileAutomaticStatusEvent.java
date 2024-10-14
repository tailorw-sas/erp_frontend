package com.kynsoft.finamer.invoicing.domain.event.importStatus;

import com.kynsoft.finamer.invoicing.domain.dto.InvoiceReconcileAutomaticImportProcessDto;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceReconcileImportProcessStatusDto;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.automatic.InvoiceReconcileAutomaticImportProcessStatusRedisEntity;
import lombok.Data;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CreateImportReconcileAutomaticStatusEvent extends ApplicationEvent {
    private InvoiceReconcileAutomaticImportProcessDto status;

    public CreateImportReconcileAutomaticStatusEvent(Object source, InvoiceReconcileAutomaticImportProcessDto statusDto) {
        super(source);
        this.status=statusDto;
    }
}
