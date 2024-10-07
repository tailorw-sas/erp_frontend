package com.kynsoft.finamer.invoicing.domain.dto;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EProcessStatus;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.InvoiceReconcileImportProcessStatusRedisEntity;
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

    public InvoiceReconcileImportProcessStatusRedisEntity toAggregate(InvoiceReconcileAutomaticImportProcessDto invoiceReconcileAutomaticImportProcessDto){
        return  InvoiceReconcileImportProcessStatusRedisEntity.builder()
                .importProcessId(invoiceReconcileAutomaticImportProcessDto.importProcessId)
                .exceptionMessage(invoiceReconcileAutomaticImportProcessDto.exceptionMessage)
                .hasError(invoiceReconcileAutomaticImportProcessDto.hasError)
                .totalRows(total)
                .build();
    }
}
