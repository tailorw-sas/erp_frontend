package com.kynsoft.finamer.creditcard.application.query.bankReconciliationStatusHistory;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.creditcard.domain.dto.BankReconciliationStatusHistoryDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class BankReconciliationStatusHistoryResponse implements IResponse {

    private UUID id;
    private ReconcileStatusToStatusHistoryResponse reconcileStatus;
    private String description;
    private LocalDateTime createdAt;
    private String employee;
    private BankReconciliationToStatusHistoryResponse bankReconciliation;

    public BankReconciliationStatusHistoryResponse(BankReconciliationStatusHistoryDto dto){
        this.id = dto.getId();
        this.reconcileStatus = dto.getReconcileTransactionStatus() != null ? new ReconcileStatusToStatusHistoryResponse(dto.getReconcileTransactionStatus()) : null;
        this.description = dto.getDescription();
        this.createdAt = dto.getCreatedAt();
        this.employee = dto.getEmployee();
        this.bankReconciliation = dto.getBankReconciliation() != null ? new BankReconciliationToStatusHistoryResponse(dto.getBankReconciliation()) : null;
    }
}
