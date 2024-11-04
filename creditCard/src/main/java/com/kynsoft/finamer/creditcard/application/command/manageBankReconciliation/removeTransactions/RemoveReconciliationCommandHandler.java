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
        double details = bankReconciliationDto != null ? bankReconciliationDto.getDetailsAmount() : 0;
        for (Long id : command.getTransactionsIds()){
            TransactionDto transactionDto = this.transactionService.findById(id);
            if (bankReconciliationDto != null && transactionDto.getReconciliation().getId().equals(command.getBankReconciliation())){
                if (transactionDto.isAdjustment()){
                    transactionDto.setReconciliation(null);
                    details -= transactionDto.getAmount();
                    this.transactionService.update(transactionDto);
                    this.transactionService.delete(transactionDto);
                } else {
                    transactionDto.setReconciliation(null);
                    details -= transactionDto.getAmount();
                    this.transactionService.update(transactionDto);
                }
            }
        }
        //si actualizo con el mismo bankReconciliationDto me volveria a cargar las transacciones
        if (bankReconciliationDto != null && !command.getTransactionsIds().isEmpty()) {
            ManageBankReconciliationDto dto = this.bankReconciliationService.findById(command.getBankReconciliation());
            dto.setDetailsAmount(details);
            this.bankReconciliationService.update(dto);
        }
    }
}
