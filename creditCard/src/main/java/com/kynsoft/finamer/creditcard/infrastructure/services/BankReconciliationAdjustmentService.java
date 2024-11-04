package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.create.CreateBankReconciliationAdjustmentRequest;
import com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.update.UpdateBankReconciliationAdjustmentRequest;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.ETransactionStatus;
import com.kynsoft.finamer.creditcard.domain.rules.adjustmentTransaction.AdjustmentTransactionAmountRule;
import com.kynsoft.finamer.creditcard.domain.services.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class BankReconciliationAdjustmentService implements IBankReconciliationAdjustmentService {

    private final IManageAgencyService agencyService;

    private final ITransactionService transactionService;

    private final IManageTransactionStatusService transactionStatusService;

    private final IManageVCCTransactionTypeService transactionTypeService;

    private final IManageBankReconciliationService bankReconciliationService;

    public BankReconciliationAdjustmentService(IManageAgencyService agencyService, ITransactionService transactionService, IManageTransactionStatusService transactionStatusService, IManageVCCTransactionTypeService transactionTypeService, IManageBankReconciliationService bankReconciliationService) {
        this.agencyService = agencyService;
        this.transactionService = transactionService;
        this.transactionStatusService = transactionStatusService;
        this.transactionTypeService = transactionTypeService;
        this.bankReconciliationService = bankReconciliationService;
    }

    @Override
    public List<Long> createAdjustments(List<CreateBankReconciliationAdjustmentRequest> adjustmentRequest, Set<TransactionDto> transactionList) {
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

    @Override
    public List<Long> createAdjustments(List<UpdateBankReconciliationAdjustmentRequest> adjustmentRequest, ManageBankReconciliationDto reconciliationDto) {
        List<Long> ids = new ArrayList<>();
        Set<TransactionDto> adjustmentTransactions = new HashSet<>();
        for (UpdateBankReconciliationAdjustmentRequest request : adjustmentRequest) {
            RulesChecker.checkRule(new AdjustmentTransactionAmountRule(request.getAmount()));

            ManageAgencyDto agencyDto = this.agencyService.findById(request.getAgency());
            ManageTransactionStatusDto transactionStatusDto = this.transactionStatusService.findByETransactionStatus(ETransactionStatus.RECEIVE);
            ManageVCCTransactionTypeDto transactionCategory = this.transactionTypeService.findById(request.getTransactionCategory());
            ManageVCCTransactionTypeDto transactionSubCategory = this.transactionTypeService.findById(request.getTransactionSubCategory());

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
                    request.getAmount(),
                    LocalDate.now(),
                    false,
                    true
            ));
            adjustmentTransactions.add(transactionDto);
            ids.add(transactionDto.getId());
        }
        reconciliationDto.getTransactions().addAll(adjustmentTransactions);
        this.bankReconciliationService.update(reconciliationDto);
        return ids;
    }
}
