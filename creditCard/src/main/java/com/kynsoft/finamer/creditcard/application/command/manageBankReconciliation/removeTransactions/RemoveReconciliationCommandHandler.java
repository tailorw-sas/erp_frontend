package com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.removeTransactions;

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
        ManageBankReconciliationDto bankReconciliationDto = command.getBankReconciliation() != null ? this.bankReconciliationService.findById(command.getBankReconciliation()) : null;
        for (Long id : command.getTransactionsIds()){
            TransactionDto transactionDto = this.transactionService.findById(id);
            if (bankReconciliationDto != null && transactionDto.getReconciliation().getId().equals(command.getBankReconciliation())){
                if (transactionDto.isAdjustment()){
                    if (bankReconciliationDto.getTransactions().remove(transactionDto)){
                        transactionDto.setReconciliation(null);
                        this.transactionService.update(transactionDto);
                        this.transactionService.delete(transactionDto);
                        bankReconciliationDto.setDetailsAmount(bankReconciliationDto.getDetailsAmount() - transactionDto.getAmount());
                    }
                } else {
                    if (bankReconciliationDto.getTransactions().remove(transactionDto)){
                        transactionDto.setReconciliation(null);
                        this.transactionService.update(transactionDto);
                        bankReconciliationDto.setDetailsAmount(bankReconciliationDto.getDetailsAmount() - transactionDto.getAmount());
                    }
                }
            }
        }
        this.bankReconciliationService.update(bankReconciliationDto);
    }
}
