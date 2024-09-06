package com.kynsoft.finamer.invoicing.infrastructure.event.importStatus;

import com.kynsoft.finamer.invoicing.domain.dto.InvoiceReconcileImportProcessStatusDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EProcessStatus;
import com.kynsoft.finamer.invoicing.domain.event.importStatus.CreateImportStatusEvent;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.InvoiceReconcileImportProcessStatusRedisEntity;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.reconcile.InvoiceReconcileImportProcessStatusRedisRepository;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CreateImportStatusEventHandler implements ApplicationListener<CreateImportStatusEvent> {

    private final InvoiceReconcileImportProcessStatusRedisRepository statusRedisRepository;

    public CreateImportStatusEventHandler(InvoiceReconcileImportProcessStatusRedisRepository statusRedisRepository) {
        this.statusRedisRepository = statusRedisRepository;
    }

    @Override
    public void onApplicationEvent(CreateImportStatusEvent event) {
        InvoiceReconcileImportProcessStatusDto dto = event.getProcessStatusDto();
        Optional<InvoiceReconcileImportProcessStatusRedisEntity> statusRedisEntity =statusRedisRepository.findByImportProcessId(dto.getImportProcessId());
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
