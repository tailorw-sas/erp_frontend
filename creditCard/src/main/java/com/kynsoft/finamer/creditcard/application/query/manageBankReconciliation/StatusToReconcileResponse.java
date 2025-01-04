package com.kynsoft.finamer.creditcard.application.query.manageBankReconciliation;

import com.kynsoft.finamer.creditcard.domain.dto.ManageReconcileTransactionStatusDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class StatusToReconcileResponse {

    private UUID id;
    private String code;
    private String name;
    private boolean completed;
    private boolean cancelled;

    public StatusToReconcileResponse(ManageReconcileTransactionStatusDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.completed = dto.isCompleted();
        this.cancelled = dto.isCancelled();
    }
}
