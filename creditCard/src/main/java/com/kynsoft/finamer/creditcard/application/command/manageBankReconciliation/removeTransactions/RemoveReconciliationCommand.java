package com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.removeTransactions;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class RemoveReconciliationCommand implements ICommand {

    private UUID bankReconciliation;
    private List<Long> transactionsIds;
    private double detailsAmount;

    public RemoveReconciliationCommand(UUID bankReconciliation, List<Long> transactionsIds) {
        this.bankReconciliation = bankReconciliation;
        this.transactionsIds = transactionsIds;
    }

    public static RemoveReconciliationCommand fromRequest(RemoveReconciliationRequest request){
        return new RemoveReconciliationCommand(
                request.getBankReconciliation(),
                request.getTransactionsIds()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new RemoveReconciliationMessage(bankReconciliation, transactionsIds, detailsAmount);
    }
}
