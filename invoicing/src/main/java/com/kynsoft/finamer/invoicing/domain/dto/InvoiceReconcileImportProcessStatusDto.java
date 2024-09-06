package com.kynsoft.finamer.invoicing.domain.dto;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EProcessStatus;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.InvoiceReconcileImportProcessStatusRedisEntity;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
public class InvoiceReconcileImportProcessStatusDto {
    @Id
    private String id;
    @Indexed
    private String importProcessId;
    private EProcessStatus status;
    private boolean hasError;
    private String exceptionMessage;

    public InvoiceReconcileImportProcessStatusRedisEntity toAggregate(){
        return new InvoiceReconcileImportProcessStatusRedisEntity(id,importProcessId,status,hasError,exceptionMessage);
    }
}
