package com.kynsoft.finamer.invoicing.infrastructure.event.importStatus;

import com.kynsoft.finamer.invoicing.domain.dto.InvoiceReconcileAutomaticImportProcessDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EProcessStatus;
import com.kynsoft.finamer.invoicing.domain.event.importStatus.CreateImportReconcileAutomaticStatusEvent;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.automatic.InvoiceReconcileAutomaticImportProcessStatusRedisEntity;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.reconcileAutomatic.reconcile.InvoiceReconcileAutomaticImportProcessStatusRedisRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CreateImportReconcileAutomaticStatusEventHandler implements ApplicationListener<CreateImportReconcileAutomaticStatusEvent> {

    private final InvoiceReconcileAutomaticImportProcessStatusRedisRepository statusRedisRepository;

    public CreateImportReconcileAutomaticStatusEventHandler(InvoiceReconcileAutomaticImportProcessStatusRedisRepository statusRedisRepository) {
        this.statusRedisRepository = statusRedisRepository;
    }


    @Override
    public void onApplicationEvent(CreateImportReconcileAutomaticStatusEvent event) {
        InvoiceReconcileAutomaticImportProcessDto dto = event.getStatus();
        Optional<InvoiceReconcileAutomaticImportProcessStatusRedisEntity> statusRedisEntity =statusRedisRepository.findByImportProcessId(dto.getImportProcessId());
        if (statusRedisEntity.isPresent()){
            if (!EProcessStatus.FINISHED.equals(statusRedisEntity.get().getStatus())) {
                statusRedisEntity.get().setStatus(dto.getStatus());
                statusRedisEntity.get().setHasError(dto.isHasError());
                statusRedisEntity.get().setExceptionMessage(dto.getExceptionMessage());
                statusRedisRepository.save(statusRedisEntity.get());
            }
        }else {
            statusRedisRepository.save(dto.toAggregate());
        }
    }
}
