package com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.addTransactions;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AddTransactionsMessage implements ICommandMessage {

    private final String command = "ADD_TRANSACTIONS";
    private UUID bankReconciliationId;
    private Set<Long> transactionIds;
    private List<Long> adjustmentIds;
}
