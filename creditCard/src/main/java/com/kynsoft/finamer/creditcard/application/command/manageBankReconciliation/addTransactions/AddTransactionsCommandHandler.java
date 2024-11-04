package com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.addTransactions;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageBankReconciliationDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.services.IBankReconciliationAdjustmentService;
import com.kynsoft.finamer.creditcard.domain.services.IManageBankReconciliationService;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class AddTransactionsCommandHandler implements ICommandHandler<AddTransactionsCommand> {

    private final IManageBankReconciliationService bankReconciliationService;

    private final IBankReconciliationAdjustmentService bankReconciliationAdjustmentService;

    private final ITransactionService transactionService;

    public AddTransactionsCommandHandler(IManageBankReconciliationService bankReconciliationService, IBankReconciliationAdjustmentService bankReconciliationAdjustmentService, ITransactionService transactionService) {
        this.bankReconciliationService = bankReconciliationService;
        this.bankReconciliationAdjustmentService = bankReconciliationAdjustmentService;
        this.transactionService = transactionService;
    }

    @Override
    public void handle(AddTransactionsCommand command) {
        ManageBankReconciliationDto bankReconciliationDto = this.bankReconciliationService.findById(command.getBankReconciliationId());
        Set<TransactionDto> bankReconciliationTransactions = bankReconciliationDto.getTransactions();

        for (Long transactionId : command.getTransactionIds()) {
            TransactionDto transactionDto = this.transactionService.findById(transactionId);
            bankReconciliationTransactions.add(transactionDto);
        }

        if (command.getAdjustmentRequests() != null && !command.getAdjustmentRequests().isEmpty()) {
            List<Long> adjustmentIds = this.bankReconciliationAdjustmentService.createAdjustments(command.getAdjustmentRequests(), bankReconciliationDto);
            command.setAdjustmentIds(adjustmentIds);
        }

        bankReconciliationDto.setTransactions(bankReconciliationTransactions);
        this.bankReconciliationService.update(bankReconciliationDto);
    }
}
