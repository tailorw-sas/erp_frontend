package com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.addTransactions;

import com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.update.UpdateBankReconciliationAdjustmentRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class AddTransactionsRequest {

    private UUID bankReconciliationId;
    private Set<Long> transactionIds;
    private List<UpdateBankReconciliationAdjustmentRequest>  adjustmentRequests;
}
