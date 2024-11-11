package com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.EReconcileTransactionStatus;
import com.kynsoft.finamer.creditcard.domain.rules.manageBankReconciliation.BankReconciliationAmountDetailsRule;
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

    public CreateBankReconciliationCommandHandler(IManageBankReconciliationService bankReconciliationService, IManageMerchantBankAccountService merchantBankAccountService, IManageHotelService hotelService, ITransactionService transactionService, IManageReconcileTransactionStatusService reconcileTransactionStatusService, IBankReconciliationAdjustmentService bankReconciliationAdjustmentService) {
        this.bankReconciliationService = bankReconciliationService;
        this.merchantBankAccountService = merchantBankAccountService;
        this.hotelService = hotelService;
        this.transactionService = transactionService;
        this.reconcileTransactionStatusService = reconcileTransactionStatusService;
        this.bankReconciliationAdjustmentService = bankReconciliationAdjustmentService;
    }

    @Override
    public void handle(CreateBankReconciliationCommand command) {
        RulesChecker.checkRule(new BankReconciliationAmountDetailsRule(command.getAmount(), command.getDetailsAmount()));
        ManageMerchantBankAccountDto merchantBankAccountDto = this.merchantBankAccountService.findById(command.getMerchantBankAccount());
        ManageHotelDto hotelDto = this.hotelService.findById(command.getHotel());

        Set<TransactionDto> transactionList = new HashSet<>();
        if (command.getTransactions() != null) {
            for (Long transactionId : command.getTransactions()) {
                TransactionDto transactionDto = this.transactionService.findById(transactionId);
                transactionList.add(transactionDto);
            }
        }

        if (command.getAdjustmentTransactions() != null) {
            List<Long> adjustmentIds = this.bankReconciliationAdjustmentService.createAdjustments(command.getAdjustmentTransactions(), transactionList);
            command.setAdjustmentTransactionIds(adjustmentIds);
        }

        //todo: reconcile status
        ManageReconcileTransactionStatusDto reconcileTransactionStatusDto = this.reconcileTransactionStatusService.findByEReconcileTransactionStatus(EReconcileTransactionStatus.CREATED);

        ManageBankReconciliationDto bankReconciliationDto = new ManageBankReconciliationDto(
                command.getId(),
                0L,
                merchantBankAccountDto,
                hotelDto,
                command.getAmount(),
                command.getDetailsAmount(),
                command.getPaidDate(),
                command.getRemark(),
                reconcileTransactionStatusDto,
                transactionList
        );

        ManageBankReconciliationDto created = this.bankReconciliationService.create(bankReconciliationDto);
        command.setReconciliationId(created.getReconciliationId());
    }

}
