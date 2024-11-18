package com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.addTransactions;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AddTransactionsAdjustmentRequest {

    private UUID agency;
    private UUID transactionCategory;
    private UUID transactionSubCategory;
    private Double amount;
    private String reservationNumber;
    private String referenceNumber;
}
