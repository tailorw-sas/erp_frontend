package com.kynsoft.finamer.creditcard.application.query.objectResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class TransactionBasicResponse {

    private Long id;
    private LocalDateTime transactionDate;
    private LocalDateTime checkIn;
}
