package com.kynsoft.finamer.invoicing.infrastructure.event.importError;

import com.kynsoft.finamer.invoicing.domain.event.importError.CreateImportErrorEvent;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.InvoiceReconcileImportError;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.reconcile.InvoiceReconcileImportErrorRedisRepository;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class CreateImportErrorEventHandler implements ApplicationListener<CreateImportErrorEvent> {
    private final InvoiceReconcileImportErrorRedisRepository importErrorRedisRepository;
    public CreateImportErrorEventHandler(InvoiceReconcileImportErrorRedisRepository importErrorRedisRepository) {
        this.importErrorRedisRepository = importErrorRedisRepository;
    }

    @Override
    public void onApplicationEvent(CreateImportErrorEvent event) {
        InvoiceReconcileImportError error = event.getReconcileImportError();
        importErrorRedisRepository.save(error);
    }
}
