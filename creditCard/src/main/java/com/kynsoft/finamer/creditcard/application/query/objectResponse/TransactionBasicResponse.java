package com.kynsoft.finamer.creditcard.application.query.objectResponse;

import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class TransactionBasicResponse {
    private Long id;
    private LocalDateTime transactionDate;
    private LocalDateTime checkIn;

    public TransactionBasicResponse(TransactionDto dto) {
        this.id = dto.getId();
        this.transactionDate = dto.getTransactionDate();
        this.checkIn = dto.getCheckIn();
    }
}
