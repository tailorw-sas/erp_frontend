package com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.addTransactions;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.update.UpdateBankReconciliationAdjustmentRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class AddTransactionsCommand implements ICommand {

    private UUID bankReconciliationId;
    private Set<Long> transactionIds;
    private List<UpdateBankReconciliationAdjustmentRequest> adjustmentRequests;
    private List<Long> adjustmentIds;

    public AddTransactionsCommand(UUID bankReconciliationId, Set<Long> transactionIds, List<UpdateBankReconciliationAdjustmentRequest> adjustmentRequests) {
        this.bankReconciliationId = bankReconciliationId;
        this.transactionIds = transactionIds;
        this.adjustmentRequests = adjustmentRequests;
    }

    public static AddTransactionsCommand fromRequest(AddTransactionsRequest request) {
        return new AddTransactionsCommand(request.getBankReconciliationId(), request.getTransactionIds(), request.getAdjustmentRequests());
    }

    @Override
    public ICommandMessage getMessage() {
        return new AddTransactionsMessage(bankReconciliationId, transactionIds, adjustmentIds);
    }
}
