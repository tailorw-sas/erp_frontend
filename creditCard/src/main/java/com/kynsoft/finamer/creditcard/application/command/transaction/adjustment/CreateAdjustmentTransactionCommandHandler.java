package com.kynsoft.finamer.creditcard.application.command.transaction.adjustment;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.ETransactionStatus;
import com.kynsoft.finamer.creditcard.domain.rules.adjustmentTransaction.AdjustmentTransactionAmountRule;
import com.kynsoft.finamer.creditcard.domain.rules.adjustmentTransaction.AdjustmentTransactionReferenceNumberMustBeNullRule;
import com.kynsoft.finamer.creditcard.domain.services.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CreateAdjustmentTransactionCommandHandler implements ICommandHandler<CreateAdjustmentTransactionCommand> {

    private final ITransactionService service;

    private final IManageAgencyService agencyService;

    private final IManageVCCTransactionTypeService transactionTypeService;

    private final IManageTransactionStatusService transactionStatusService;

    private final ITransactionStatusHistoryService transactionStatusHistoryService;

    public CreateAdjustmentTransactionCommandHandler(ITransactionService service, IManageAgencyService agencyService, IManageVCCTransactionTypeService transactionTypeService, IManageTransactionStatusService transactionStatusService, ITransactionStatusHistoryService transactionStatusHistoryService) {
        this.service = service;
        this.agencyService = agencyService;
        this.transactionTypeService = transactionTypeService;
        this.transactionStatusService = transactionStatusService;
        this.transactionStatusHistoryService = transactionStatusHistoryService;
    }

    @Override
    public void handle(CreateAdjustmentTransactionCommand command) {
        RulesChecker.checkRule(new AdjustmentTransactionAmountRule(command.getAmount()));
        RulesChecker.checkRule(new AdjustmentTransactionReferenceNumberMustBeNullRule(command.getReferenceNumber()));

        ManageAgencyDto agencyDto = this.agencyService.findById(command.getAgency());

//        if(!command.getReservationNumber().isBlank()) {
//            RulesChecker.checkRule(new AdjustmentTransactionAgencyBookingFormatRule(agencyDto.getBookingCouponFormat()));
//            RulesChecker.checkRule(new AdjustmentTransactionReservationNumberRule(command.getReservationNumber(), agencyDto.getBookingCouponFormat()));
//        }

        ManageTransactionStatusDto transactionStatusDto = this.transactionStatusService.findByETransactionStatus(ETransactionStatus.SENT);
        ManageVCCTransactionTypeDto transactionCategory = this.transactionTypeService.findById(command.getTransactionCategory());
        ManageVCCTransactionTypeDto transactionSubCategory = this.transactionTypeService.findById(command.getTransactionSubCategory());

        double amount = transactionCategory.getOnlyApplyNet() ? 0.0 : command.getAmount();
        double netAmount = command.getAmount();

        TransactionDto transactionDto = this.service.create(new TransactionDto(
                command.getTransactionUuid(),
                agencyDto,
                transactionCategory,
                transactionSubCategory,
                amount,
                command.getReservationNumber(),
                command.getReferenceNumber(),
                transactionStatusDto,
                0.0,
                LocalDateTime.now(),
                netAmount,
                null,
                false,
                true
        ));
        command.setId(transactionDto.getId());
        this.transactionStatusHistoryService.create(new TransactionStatusHistoryDto(
                UUID.randomUUID(),
                transactionDto,
                "The transaction status is "+transactionStatusDto.getCode() + "-" +transactionStatusDto.getName()+".",
                null,
                command.getEmployee(),
                transactionStatusDto
        ));
    }
}
