package com.kynsoft.finamer.invoicing.infrastructure.event.importError;

import com.kynsoft.finamer.invoicing.domain.event.importError.CreateImportErrorEvent;
import com.kynsoft.finamer.invoicing.domain.event.importError.CreateImportReconcileAutomaticErrorEvent;
import com.kynsoft.finamer.invoicing.domain.event.importStatus.CreateImportReconcileAutomaticStatusEvent;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.InvoiceReconcileImportError;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.automatic.InvoiceReconcileAutomaticImportErrorEntity;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.reconcile.InvoiceReconcileImportErrorRedisRepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.reconcileAutomatic.reconcile.InvoiceReconcileAutomaticImportErrorRedisRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class CreateImportReconcileAutomaticErrorEventHandler implements ApplicationListener<CreateImportReconcileAutomaticErrorEvent> {
    private final InvoiceReconcileAutomaticImportErrorRedisRepository importErrorRedisRepository;
    public CreateImportReconcileAutomaticErrorEventHandler(InvoiceReconcileAutomaticImportErrorRedisRepository importErrorRedisRepository) {
        this.importErrorRedisRepository = importErrorRedisRepository;
    }

    @Override
    public void onApplicationEvent(CreateImportReconcileAutomaticErrorEvent event) {
        InvoiceReconcileAutomaticImportErrorEntity error = event.getReconcileImportError();
        importErrorRedisRepository.save(error);
    }
}
