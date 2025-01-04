package com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.addTransactions;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.finamer.creditcard.domain.dto.ManageBankReconciliationDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.rules.manageBankReconciliation.BankReconciliationAmountDetailsRule;
import com.kynsoft.finamer.creditcard.domain.rules.transaction.TransactionInReconciliationRule;
import com.kynsoft.finamer.creditcard.domain.services.IBankReconciliationAdjustmentService;
import com.kynsoft.finamer.creditcard.domain.services.IManageBankReconciliationService;
import com.kynsoft.finamer.creditcard.domain.services.IParameterizationService;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AddTransactionsCommandHandler implements ICommandHandler<AddTransactionsCommand> {

    private final IManageBankReconciliationService bankReconciliationService;

    private final IBankReconciliationAdjustmentService bankReconciliationAdjustmentService;

    private final ITransactionService transactionService;

    private final IParameterizationService parameterizationService;

    public AddTransactionsCommandHandler(IManageBankReconciliationService bankReconciliationService, IBankReconciliationAdjustmentService bankReconciliationAdjustmentService, ITransactionService transactionService, IParameterizationService parameterizationService) {
        this.bankReconciliationService = bankReconciliationService;
        this.bankReconciliationAdjustmentService = bankReconciliationAdjustmentService;
        this.transactionService = transactionService;
        this.parameterizationService = parameterizationService;
    }

    @Override
    public void handle(AddTransactionsCommand command) {
        ManageBankReconciliationDto bankReconciliationDto = this.bankReconciliationService.findById(command.getBankReconciliationId());
        Set<TransactionDto> bankReconciliationTransactions = bankReconciliationDto.getTransactions();
        Set<Long> reconcileTransactions = bankReconciliationDto.getTransactions().stream().map(TransactionDto::getId).collect(Collectors.toSet());

        //obtener la suma de los amounts si no pertenecen ya a la reconciliacion
        Double newDetails = bankReconciliationDto.getDetailsAmount() + command.getTransactionIds().stream().map(id -> {
            if(!reconcileTransactions.contains(id)) {
                return this.transactionService.findById(id).getNetAmount();
            } return 0.0;
        }).reduce(0.0, Double::sum);

        //comprobar que se inserten nuevos registros
        int cont = 0;
        for (Long transactionId : command.getTransactionIds()) {
            if (!reconcileTransactions.contains(transactionId)) {
                TransactionDto transactionDto = this.transactionService.findById(transactionId);
                RulesChecker.checkRule(new TransactionInReconciliationRule(transactionDto));
                bankReconciliationTransactions.add(transactionDto);
                bankReconciliationDto.setDetailsAmount(bankReconciliationDto.getDetailsAmount() + transactionDto.getNetAmount());
                cont++;
            } else{
                throw new BusinessException(
                        DomainErrorMessage.TRANSACTION_ALREADY_IN_THIS_RELATION,
                        DomainErrorMessage.TRANSACTION_ALREADY_IN_THIS_RELATION.getReasonPhrase()
                );
            }
        }

        if (command.getAdjustmentRequests() != null && !command.getAdjustmentRequests().isEmpty()) {
            List<Long> adjustmentIds = this.bankReconciliationAdjustmentService.createAdjustments(command.getAdjustmentRequests(), bankReconciliationDto);
            command.setAdjustmentIds(adjustmentIds);
            cont++;
        } else {
            RulesChecker.checkRule(new BankReconciliationAmountDetailsRule(bankReconciliationDto.getAmount(), newDetails, this.parameterizationService));
        }

        if (cont > 0) {
            bankReconciliationDto.setTransactions(bankReconciliationTransactions);
            this.bankReconciliationService.update(bankReconciliationDto);
        }
        command.setDetailsAmount(bankReconciliationDto.getDetailsAmount());
    }
}
