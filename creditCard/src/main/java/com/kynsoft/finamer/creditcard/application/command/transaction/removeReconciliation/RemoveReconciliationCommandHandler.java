package com.kynsoft.finamer.creditcard.application.command.transaction.removeReconciliation;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageBankReconciliationDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageBankReconciliationService;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;
import org.springframework.stereotype.Component;

@Component
public class RemoveReconciliationCommandHandler implements ICommandHandler<RemoveReconciliationCommand> {

    private final ITransactionService transactionService;

    private final IManageBankReconciliationService bankReconciliationService;

    public RemoveReconciliationCommandHandler(ITransactionService transactionService, IManageBankReconciliationService bankReconciliationService) {
        this.transactionService = transactionService;
        this.bankReconciliationService = bankReconciliationService;
    }

    @Override
    public void handle(RemoveReconciliationCommand command) {
        ManageBankReconciliationDto bankReconciliationDto = this.bankReconciliationService.findById(command.getBankReconciliation());

        for (Long id : command.getTransactionsIds()){
            TransactionDto transactionDto = this.transactionService.findById(id);
            if (transactionDto.isAdjustment()){
                transactionDto.setReconciliation(null);
                this.transactionService.update(transactionDto);
                this.transactionService.delete(transactionDto);
            } else {
                transactionDto.setReconciliation(null);
                this.transactionService.update(transactionDto);
            }
        }
    }
}
