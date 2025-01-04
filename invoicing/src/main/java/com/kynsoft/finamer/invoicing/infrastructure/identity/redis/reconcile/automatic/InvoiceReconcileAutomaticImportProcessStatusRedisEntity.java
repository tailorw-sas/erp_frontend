package com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.automatic;

import com.kynsoft.finamer.invoicing.domain.dto.InvoiceReconcileAutomaticImportProcessDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EProcessStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@Builder
@RedisHash(value = "reconcileautomaticimportprocess",timeToLive = 3600)
public class InvoiceReconcileAutomaticImportProcessStatusRedisEntity {
    @Id
    private String id;
    @Indexed
    private String importProcessId;
    private EProcessStatus status;
    private boolean hasError;
    private String exceptionMessage;
    private int total;

    private InvoiceReconcileAutomaticImportProcessDto toAggregate(){
        return InvoiceReconcileAutomaticImportProcessDto
                .builder()
                .importProcessId(importProcessId)
                .total(total)
                .exceptionMessage(exceptionMessage)
                .hasError(hasError)
                .id(id).build();
    }
}
