package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsoft.finamer.creditcard.application.command.hotelPayment.create.CreateHotelPaymentAdjustmentRequest;
import com.kynsoft.finamer.creditcard.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageVCCTransactionTypeDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.ETransactionStatus;
import com.kynsoft.finamer.creditcard.domain.rules.adjustmentTransaction.AdjustmentTransactionAmountRule;
import com.kynsoft.finamer.creditcard.domain.rules.adjustmentTransaction.AdjustmentTransactionReferenceNumberMustBeNullRule;
import com.kynsoft.finamer.creditcard.domain.services.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class HotelPaymentAdjustmentServiceImpl implements IHotelPaymentAdjustmentService {

    private final IManageAgencyService agencyService;

    private final ITransactionService transactionService;

    private final IManageTransactionStatusService transactionStatusService;

    private final IManageVCCTransactionTypeService transactionTypeService;

    private final ITransactionStatusHistoryService transactionStatusHistoryService;

    public HotelPaymentAdjustmentServiceImpl(IManageAgencyService agencyService, ITransactionService transactionService, IManageTransactionStatusService transactionStatusService, IManageVCCTransactionTypeService transactionTypeService, ITransactionStatusHistoryService transactionStatusHistoryService) {
        this.agencyService = agencyService;
        this.transactionService = transactionService;
        this.transactionStatusService = transactionStatusService;
        this.transactionTypeService = transactionTypeService;
        this.transactionStatusHistoryService = transactionStatusHistoryService;
    }

    @Override
    @Transactional
    public List<Long> createAdjustments(List<CreateHotelPaymentAdjustmentRequest> adjustmentRequest, Set<TransactionDto> transactionList) {
        adjustmentRequest.forEach(obj -> {
            RulesChecker.checkRule(new AdjustmentTransactionReferenceNumberMustBeNullRule(obj.getReferenceNumber()));
            RulesChecker.checkRule(new AdjustmentTransactionAmountRule(obj.getAmount()));
        });

        List<Long> ids = new ArrayList<>();
        for (CreateHotelPaymentAdjustmentRequest request : adjustmentRequest) {
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
            this.transactionStatusHistoryService.create(transactionDto);
        }
        return ids;
    }
}
