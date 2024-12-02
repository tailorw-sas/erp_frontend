package com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.EReconcileTransactionStatus;
import com.kynsoft.finamer.creditcard.domain.rules.manageBankReconciliation.BankReconciliationAmountDetailsRule;
import com.kynsoft.finamer.creditcard.domain.rules.manageBankReconciliation.TransactionCheckInCloseOperationRule;
import com.kynsoft.finamer.creditcard.domain.rules.transaction.TransactionReconciliationOrPaymentRule;
import com.kynsoft.finamer.creditcard.domain.services.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CreateBankReconciliationCommandHandler implements ICommandHandler<CreateBankReconciliationCommand> {

    private final IManageBankReconciliationService bankReconciliationService;

    private final IManageMerchantBankAccountService merchantBankAccountService;

    private final IManageHotelService hotelService;

    private final ITransactionService transactionService;

    private final IManageReconcileTransactionStatusService reconcileTransactionStatusService;

    private final IBankReconciliationAdjustmentService bankReconciliationAdjustmentService;

    private final IBankReconciliationStatusHistoryService bankReconciliationStatusHistoryService;

    private final IParameterizationService parameterizationService;

    private final ICreditCardCloseOperationService closeOperationService;

    public CreateBankReconciliationCommandHandler(IManageBankReconciliationService bankReconciliationService, IManageMerchantBankAccountService merchantBankAccountService, IManageHotelService hotelService, ITransactionService transactionService, IManageReconcileTransactionStatusService reconcileTransactionStatusService, IBankReconciliationAdjustmentService bankReconciliationAdjustmentService, IBankReconciliationStatusHistoryService bankReconciliationStatusHistoryService, IParameterizationService parameterizationService, ICreditCardCloseOperationService closeOperationService) {
        this.bankReconciliationService = bankReconciliationService;
        this.merchantBankAccountService = merchantBankAccountService;
        this.hotelService = hotelService;
        this.transactionService = transactionService;
        this.reconcileTransactionStatusService = reconcileTransactionStatusService;
        this.bankReconciliationAdjustmentService = bankReconciliationAdjustmentService;
        this.bankReconciliationStatusHistoryService = bankReconciliationStatusHistoryService;
        this.parameterizationService = parameterizationService;
        this.closeOperationService = closeOperationService;
    }

    @Override
    public void handle(CreateBankReconciliationCommand command) {
        RulesChecker.checkRule(new TransactionCheckInCloseOperationRule(this.closeOperationService, command.getPaidDate(), command.getHotel()));
        ManageMerchantBankAccountDto merchantBankAccountDto = this.merchantBankAccountService.findById(command.getMerchantBankAccount());
        ManageHotelDto hotelDto = this.hotelService.findById(command.getHotel());

        Set<TransactionDto> transactionList = new HashSet<>();
        if (command.getTransactions() != null) {
            for (Long transactionId : command.getTransactions()) {
                TransactionDto transactionDto = this.transactionService.findById(transactionId);
                RulesChecker.checkRule(new TransactionReconciliationOrPaymentRule(transactionDto));
                transactionList.add(transactionDto);
            }
        }

        Double detailsAmount = transactionList.stream().map(TransactionDto::getNetAmount).reduce(0.0, Double::sum);

        if (command.getAdjustmentTransactions() != null) {
            List<Long> adjustmentIds = this.bankReconciliationAdjustmentService.createAdjustments(command.getAdjustmentTransactions(), transactionList, command.getAmount(), detailsAmount);
            command.setAdjustmentTransactionIds(adjustmentIds);
        } else {
            RulesChecker.checkRule(new BankReconciliationAmountDetailsRule(command.getAmount(), detailsAmount, this.parameterizationService));
        }

        detailsAmount = transactionList.stream().map(transactionDto ->
                transactionDto.isAdjustment()
                    ? transactionDto.getTransactionSubCategory().getNegative()
                        ? -transactionDto.getNetAmount()
                        : transactionDto.getNetAmount()
                    : transactionDto.getNetAmount()
        ).reduce(0.0, Double::sum);

        //todo: reconcile status
        ManageReconcileTransactionStatusDto reconcileTransactionStatusDto = this.reconcileTransactionStatusService.findByEReconcileTransactionStatus(EReconcileTransactionStatus.CREATED);

        ManageBankReconciliationDto bankReconciliationDto = new ManageBankReconciliationDto(
                command.getId(),
                0L,
                merchantBankAccountDto,
                hotelDto,
                command.getAmount(),
                detailsAmount,
                command.getPaidDate(),
                command.getRemark(),
                reconcileTransactionStatusDto,
                transactionList
        );

        ManageBankReconciliationDto created = this.bankReconciliationService.create(bankReconciliationDto);
        command.setReconciliationId(created.getReconciliationId());
        this.bankReconciliationStatusHistoryService.create(new BankReconciliationStatusHistoryDto(
                UUID.randomUUID(),
                created,
                "The reconcile status is "+reconcileTransactionStatusDto.getCode()+"-"+reconcileTransactionStatusDto.getName()+".",
                null,
                command.getEmployee(),
                reconcileTransactionStatusDto
        ));
    }

}
