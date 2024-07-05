package com.kynsoft.finamer.creditcard.application.query.objectResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class TransactionBasicResponse {

    private Long id;
    private LocalDate transactionDate;
    private LocalDate checkIn;
}
