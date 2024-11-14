package com.kynsoft.finamer.creditcard.application.query.transaction.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class TransactionTotalResume {
    private Double totalAmount;
    private Double commission;
    private Double netAmount;
}
