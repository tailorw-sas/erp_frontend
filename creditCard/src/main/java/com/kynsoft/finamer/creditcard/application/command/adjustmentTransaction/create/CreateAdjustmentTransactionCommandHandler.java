package com.kynsoft.finamer.creditcard.application.command.adjustmentTransaction.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.rules.adjustmentTransaction.AdjustmentTransactionAgencyBookingFormatRule;
import com.kynsoft.finamer.creditcard.domain.rules.adjustmentTransaction.AdjustmentTransactionAmountRule;
import com.kynsoft.finamer.creditcard.domain.rules.adjustmentTransaction.AdjustmentTransactionReservationNumberRule;
import com.kynsoft.finamer.creditcard.domain.services.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Objects;

@Component
public class CreateAdjustmentTransactionCommandHandler implements ICommandHandler<CreateAdjustmentTransactionCommand> {

    private final ITransactionService service;

    private final IManageAgencyService agencyService;

    private final IManageVCCTransactionTypeService transactionTypeService;

    private final IParameterizationService parameterizationService;

    private final IManageTransactionStatusService transactionStatusService;

    private final ICreditCardCloseOperationService closeOperationService;

    public CreateAdjustmentTransactionCommandHandler(ITransactionService service, IManageAgencyService agencyService, IManageVCCTransactionTypeService transactionTypeService, IParameterizationService parameterizationService, IManageTransactionStatusService transactionStatusService, ICreditCardCloseOperationService closeOperationService) {
        this.service = service;
        this.agencyService = agencyService;
        this.transactionTypeService = transactionTypeService;
        this.parameterizationService = parameterizationService;
        this.transactionStatusService = transactionStatusService;
        this.closeOperationService = closeOperationService;
    }

    @Override
    public void handle(CreateAdjustmentTransactionCommand command) {
        RulesChecker.checkRule(new AdjustmentTransactionAmountRule(command.getAmount()));

        ManageAgencyDto agencyDto = this.agencyService.findById(command.getAgency());

//        if(!command.getReservationNumber().isBlank()) {
//            RulesChecker.checkRule(new AdjustmentTransactionAgencyBookingFormatRule(agencyDto.getBookingCouponFormat()));
//            RulesChecker.checkRule(new AdjustmentTransactionReservationNumberRule(command.getReservationNumber(), agencyDto.getBookingCouponFormat()));
//        }
        ParameterizationDto parameterizationDto = this.parameterizationService.findActiveParameterization();
        if(Objects.isNull(parameterizationDto)){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", "No active parameterization")));
        }

        ManageTransactionStatusDto transactionStatusDto = this.transactionStatusService.findByCode(parameterizationDto.getTransactionStatusCode());
        ManageVCCTransactionTypeDto transactionCategory = this.transactionTypeService.findByCode(parameterizationDto.getTransactionCategory());
        ManageVCCTransactionTypeDto transactionSubCategory = this.transactionTypeService.findByCode(parameterizationDto.getTransactionSubCategory());

        double commission = 0;
        LocalDate transactionDate = command.getTransactionDate();
        double netAmount = command.getAmount() - commission;

        Long id = this.service.create(new TransactionDto(
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
