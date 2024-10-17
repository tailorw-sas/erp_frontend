package com.kynsoft.finamer.creditcard.application.command.transaction.adjustment;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.ETransactionStatus;
import com.kynsoft.finamer.creditcard.domain.rules.adjustmentTransaction.AdjustmentTransactionAmountRule;
import com.kynsoft.finamer.creditcard.domain.services.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CreateAdjustmentTransactionCommandHandler implements ICommandHandler<CreateAdjustmentTransactionCommand> {

    private final ITransactionService service;

    private final IManageAgencyService agencyService;

    private final IManageVCCTransactionTypeService transactionTypeService;

    private final IManageTransactionStatusService transactionStatusService;

    public CreateAdjustmentTransactionCommandHandler(ITransactionService service, IManageAgencyService agencyService, IManageVCCTransactionTypeService transactionTypeService, IManageTransactionStatusService transactionStatusService) {
        this.service = service;
        this.agencyService = agencyService;
        this.transactionTypeService = transactionTypeService;
        this.transactionStatusService = transactionStatusService;
    }

    @Override
    public void handle(CreateAdjustmentTransactionCommand command) {
        RulesChecker.checkRule(new AdjustmentTransactionAmountRule(command.getAmount()));

        ManageAgencyDto agencyDto = this.agencyService.findById(command.getAgency());

//        if(!command.getReservationNumber().isBlank()) {
//            RulesChecker.checkRule(new AdjustmentTransactionAgencyBookingFormatRule(agencyDto.getBookingCouponFormat()));
//            RulesChecker.checkRule(new AdjustmentTransactionReservationNumberRule(command.getReservationNumber(), agencyDto.getBookingCouponFormat()));
//        }

        ManageTransactionStatusDto transactionStatusDto = this.transactionStatusService.findByETransactionStatus(ETransactionStatus.SENT);
        ManageVCCTransactionTypeDto transactionCategory = this.transactionTypeService.findById(command.getTransactionCategory());
        ManageVCCTransactionTypeDto transactionSubCategory = this.transactionTypeService.findById(command.getTransactionSubCategory());

        double commission = 0;
        LocalDate transactionDate = command.getTransactionDate();
        double netAmount = command.getAmount() - commission;

        Long id = this.service.create(new TransactionDto(
                command.getTransactionUuid(),
                agencyDto,
                transactionCategory,
                transactionSubCategory,
                command.getAmount(),
                command.getReservationNumber(),
                command.getReferenceNumber(),
                transactionStatusDto,
                commission,
                transactionDate,
                netAmount,
                transactionDate,
                false
        ));
        command.setId(id);
    }
}
