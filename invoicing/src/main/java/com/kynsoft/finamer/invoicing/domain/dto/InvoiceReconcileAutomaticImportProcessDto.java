package com.kynsoft.finamer.invoicing.domain.dto;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EProcessStatus;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.InvoiceReconcileImportProcessStatusRedisEntity;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.automatic.InvoiceReconcileAutomaticImportProcessStatusRedisEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InvoiceReconcileAutomaticImportProcessDto {
    private String id;
    private String importProcessId;
    private EProcessStatus status;
    private int total;
    private boolean hasError;
    private String exceptionMessage;

    public InvoiceReconcileAutomaticImportProcessStatusRedisEntity toAggregate(){
        return  InvoiceReconcileAutomaticImportProcessStatusRedisEntity.builder()
                .importProcessId(importProcessId)
                .exceptionMessage(exceptionMessage)
                .hasError(hasError)
               // .totalRows(total)
                .build();
    }
}
