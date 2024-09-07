package com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "reconcileimportprocess",timeToLive = 3600)
public class InvoiceReconcileImportProcessStatusRedisEntity {
    @Id
    private String id;
    @Indexed
    private String importProcessId;
    private EProcessStatus status;
    private boolean hasError;
    private String exceptionMessage;
}
