package com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.removeTransactions;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;
import org.springframework.stereotype.Component;

@Component
public class RemoveReconciliationCommandHandler implements ICommandHandler<RemoveReconciliationCommand> {

    private final ITransactionService transactionService;

    public RemoveReconciliationCommandHandler(ITransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public void handle(RemoveReconciliationCommand command) {
        for (Long id : command.getTransactionsIds()){
            TransactionDto transactionDto = this.transactionService.findById(id);
            if (command.getBankReconciliation() != null){
                if (transactionDto.getReconciliation().getId().equals(command.getBankReconciliation())){
                    if (transactionDto.isAdjustment()){
                        transactionDto.setReconciliation(null);
                        this.transactionService.update(transactionDto);
                        this.transactionService.delete(transactionDto);
                    } else {
                        transactionDto.setReconciliation(null);
                        this.transactionService.update(transactionDto);
                    }
                }
            } else {
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
}
