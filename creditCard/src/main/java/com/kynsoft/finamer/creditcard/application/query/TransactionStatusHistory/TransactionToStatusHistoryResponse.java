package com.kynsoft.finamer.creditcard.application.query.TransactionStatusHistory;

import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class TransactionToStatusHistoryResponse {

    private Long id;
    private UUID transactionUuid;
    private LocalDateTime checkIn;
    private Double amount;
    private Double netAmount;

    public TransactionToStatusHistoryResponse(TransactionDto dto){
        this.id = dto.getId();
        this.transactionUuid = dto.getTransactionUuid();
        this.checkIn = dto.getCheckIn();
        this.amount = dto.getAmount();
        this.netAmount = dto.getNetAmount();
    }
}
