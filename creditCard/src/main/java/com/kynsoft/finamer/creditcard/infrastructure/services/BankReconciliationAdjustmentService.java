package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.create.CreateBankReconciliationAdjustmentRequest;
import com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.update.UpdateBankReconciliationAdjustmentRequest;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.ETransactionStatus;
import com.kynsoft.finamer.creditcard.domain.rules.adjustmentTransaction.AdjustmentTransactionAmountRule;
import com.kynsoft.finamer.creditcard.domain.rules.adjustmentTransaction.AdjustmentTransactionReferenceNumberMustBeNullRule;
import com.kynsoft.finamer.creditcard.domain.rules.manageBankReconciliation.BankReconciliationAmountDetailsRule;
import com.kynsoft.finamer.creditcard.domain.rules.manageBankReconciliation.BankReconciliationListOfAmountDetailsRule;
import com.kynsoft.finamer.creditcard.domain.services.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    @Transactional
    public List<Long> createAdjustments(List<CreateBankReconciliationAdjustmentRequest> adjustmentRequest, Set<TransactionDto> transactionList, Double amount, Double detailsAmount) {
        adjustmentRequest.forEach(obj -> {
            RulesChecker.checkRule(new AdjustmentTransactionReferenceNumberMustBeNullRule(obj.getReferenceNumber()));
            RulesChecker.checkRule(new AdjustmentTransactionAmountRule(obj.getAmount()));
        });

        detailsAmount += adjustmentRequest.stream().map(obj -> {
                    ManageVCCTransactionTypeDto subCategory = this.transactionTypeService.findById(obj.getTransactionSubCategory());
                    if (subCategory.getNegative()) {
                        return -obj.getAmount();
                    } else {
                        return obj.getAmount();
                    }
                }
        ).reduce(0.0, Double::sum);

        RulesChecker.checkRule(new BankReconciliationAmountDetailsRule(amount, detailsAmount));

        List<Long> ids = new ArrayList<>();
        for (CreateBankReconciliationAdjustmentRequest request : adjustmentRequest) {
            ManageAgencyDto agencyDto = this.agencyService.findById(request.getAgency());
            ManageTransactionStatusDto transactionStatusDto = this.transactionStatusService.findByETransactionStatus(ETransactionStatus.RECEIVE);
            ManageVCCTransactionTypeDto transactionCategory = this.transactionTypeService.findById(request.getTransactionCategory());
            ManageVCCTransactionTypeDto transactionSubCategory = this.transactionTypeService.findById(request.getTransactionSubCategory());

            double transactionAmount = transactionCategory.getOnlyApplyNet() ? 0.0 : request.getAmount();

            TransactionDto transactionDto = this.transactionService.create(new TransactionDto(
                    UUID.randomUUID(),
                    agencyDto,
                    transactionCategory,
                    transactionSubCategory,
                    transactionAmount,
                    request.getReservationNumber(),
                    request.getReferenceNumber(),
                    transactionStatusDto,
                    0.0,
                    LocalDateTime.now(),
                    request.getAmount(),
                    LocalDateTime.now(),
                    false,
                    true
            ));
            transactionList.add(transactionDto);
            ids.add(transactionDto.getId());
        }
        return ids;
    }

    @Override
    @Transactional
    public List<Long> createAdjustments(List<UpdateBankReconciliationAdjustmentRequest> adjustmentRequest, ManageBankReconciliationDto reconciliationDto) {
        adjustmentRequest.forEach(obj -> {
            RulesChecker.checkRule(new AdjustmentTransactionReferenceNumberMustBeNullRule(obj.getReferenceNumber()));
            RulesChecker.checkRule(new AdjustmentTransactionAmountRule(obj.getAmount()));
        });

        List<Long> ids = new ArrayList<>();
        Set<TransactionDto> adjustmentTransactions = new HashSet<>();

        Double detailsAmount = reconciliationDto.getDetailsAmount() + adjustmentRequest.stream().map(obj -> {
                    ManageVCCTransactionTypeDto subCategory = this.transactionTypeService.findById(obj.getTransactionSubCategory());
                    if (subCategory.getNegative()) {
                        return -obj.getAmount();
                    } else {
                        return obj.getAmount();
                    }
                }
        ).reduce(0.0, Double::sum);
        RulesChecker.checkRule(new BankReconciliationAmountDetailsRule(reconciliationDto.getAmount(), detailsAmount));

        for (UpdateBankReconciliationAdjustmentRequest request : adjustmentRequest) {
            ManageAgencyDto agencyDto = this.agencyService.findById(request.getAgency());
            ManageTransactionStatusDto transactionStatusDto = this.transactionStatusService.findByETransactionStatus(ETransactionStatus.RECEIVE);
            ManageVCCTransactionTypeDto transactionCategory = this.transactionTypeService.findById(request.getTransactionCategory());
            ManageVCCTransactionTypeDto transactionSubCategory = this.transactionTypeService.findById(request.getTransactionSubCategory());

            double transactionAmount = transactionCategory.getOnlyApplyNet() ? 0.0 : request.getAmount();
            TransactionDto transactionDto = this.transactionService.create(new TransactionDto(
                    UUID.randomUUID(),
                    agencyDto,
                    transactionCategory,
                    transactionSubCategory,
                    transactionAmount,
                    request.getReservationNumber(),
                    request.getReferenceNumber(),
                    transactionStatusDto,
                    0.0,
                    LocalDateTime.now(),
                    request.getAmount(),
                    LocalDateTime.now(),
                    false,
                    true
            ));
            adjustmentTransactions.add(transactionDto);
            ids.add(transactionDto.getId());
            reconciliationDto.setDetailsAmount(detailsAmount);
        }
        reconciliationDto.getTransactions().addAll(adjustmentTransactions);
        this.bankReconciliationService.update(reconciliationDto);
        return ids;
    }
}
