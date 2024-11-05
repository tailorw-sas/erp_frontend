package com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.removeTransactions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class RemoveReconciliationRequest {

    private UUID bankReconciliation;
    private List<Long> transactionsIds;
}
