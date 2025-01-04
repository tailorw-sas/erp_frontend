package com.kynsoft.finamer.creditcard.application.query.bankReconciliationStatusHistory;

import com.kynsoft.finamer.creditcard.domain.dto.ManageBankReconciliationDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class BankReconciliationToStatusHistoryResponse {

    private UUID id;
    private Long reconciliationId;
    private Double amount;
    private Double detailsAmount;

    public BankReconciliationToStatusHistoryResponse(ManageBankReconciliationDto dto){
        this.id = dto.getId();
        this.reconciliationId = dto.getReconciliationId();
        this.amount = dto.getAmount();
        this.detailsAmount = dto.getDetailsAmount();
    }
}
