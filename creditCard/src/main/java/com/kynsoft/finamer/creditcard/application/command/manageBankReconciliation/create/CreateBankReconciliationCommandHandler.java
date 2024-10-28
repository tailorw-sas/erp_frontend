package com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.ETransactionStatus;
import com.kynsoft.finamer.creditcard.domain.rules.adjustmentTransaction.AdjustmentTransactionAmountRule;
import com.kynsoft.finamer.creditcard.domain.services.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class CreateBankReconciliationCommandHandler implements ICommandHandler<CreateBankReconciliationCommand> {

    private final IManageBankReconciliationService bankReconciliationService;

    private final IManageMerchantBankAccountService merchantBankAccountService;

    private final IManageHotelService hotelService;

    private final ITransactionService transactionService;

    private final IManageAgencyService agencyService;

    private final IManageReconcileTransactionStatusService reconcileTransactionStatusService;

    private final IManageVCCTransactionTypeService transactionTypeService;

    private final IManageTransactionStatusService transactionStatusService;

    public CreateBankReconciliationCommandHandler(IManageBankReconciliationService bankReconciliationService, IManageMerchantBankAccountService merchantBankAccountService, IManageHotelService hotelService, ITransactionService transactionService, IManageAgencyService agencyService, IManageReconcileTransactionStatusService reconcileTransactionStatusService, IManageVCCTransactionTypeService transactionTypeService, IManageTransactionStatusService transactionStatusService) {
        this.bankReconciliationService = bankReconciliationService;
        this.merchantBankAccountService = merchantBankAccountService;
        this.hotelService = hotelService;
        this.transactionService = transactionService;
        this.agencyService = agencyService;
        this.reconcileTransactionStatusService = reconcileTransactionStatusService;
        this.transactionTypeService = transactionTypeService;
        this.transactionStatusService = transactionStatusService;
    }

    @Override
    public void handle(CreateBankReconciliationCommand command) {
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
            List<Long> adjustmentIds = this.createAdjustments(command.getAdjustmentTransactions(), transactionList);
            command.setAdjustmentTransactionIds(adjustmentIds);
        }

        //todo: reconcile status
        ManageReconcileTransactionStatusDto reconcileTransactionStatusDto = null;

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

    private List<Long> createAdjustments(List<CreateBankReconciliationAdjustmentRequest> adjustmentRequest, Set<TransactionDto> transactionList){
        List<Long> ids = new ArrayList<>();
        for (CreateBankReconciliationAdjustmentRequest request : adjustmentRequest) {
            RulesChecker.checkRule(new AdjustmentTransactionAmountRule(request.getAmount()));

            ManageAgencyDto agencyDto = this.agencyService.findById(request.getAgency());
            ManageTransactionStatusDto transactionStatusDto = this.transactionStatusService.findByETransactionStatus(ETransactionStatus.RECEIVE);
            ManageVCCTransactionTypeDto transactionCategory = this.transactionTypeService.findById(request.getTransactionCategory());
            ManageVCCTransactionTypeDto transactionSubCategory = this.transactionTypeService.findById(request.getTransactionSubCategory());

            //todo: calculo de la commission
            double commission = 0;
            double netAmount = request.getAmount() - commission;

            TransactionDto transactionDto = this.transactionService.create(new TransactionDto(
                    UUID.randomUUID(),
                    agencyDto,
                    transactionCategory,
                    transactionSubCategory,
                    request.getAmount(),
                    request.getReservationNumber(),
                    request.getReferenceNumber(),
                    transactionStatusDto,
                    0.0,
                    LocalDate.now(),
                    netAmount,
                    LocalDate.now(),
                    false,
                    true
            ));
            transactionList.add(transactionDto);
            ids.add(transactionDto.getId());
        }
        return ids;
    }
}
