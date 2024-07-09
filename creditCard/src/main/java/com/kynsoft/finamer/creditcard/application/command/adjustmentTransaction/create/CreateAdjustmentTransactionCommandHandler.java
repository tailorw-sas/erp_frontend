package com.kynsoft.finamer.creditcard.application.command.adjustmentTransaction.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.rules.adjustmentTransaction.AdjustmentTransactionAmountRule;
import com.kynsoft.finamer.creditcard.domain.rules.adjustmentTransaction.AdjustmentTransactionReservationNumberRule;
import com.kynsoft.finamer.creditcard.domain.services.*;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CreateAdjustmentTransactionCommandHandler implements ICommandHandler<CreateAdjustmentTransactionCommand> {

    private final ITransactionService service;

    private final IManageAgencyService agencyService;

    private final IManageVCCTransactionTypeService transactionTypeService;

    private final IParameterizationService parameterizationService;

    private final IManageTransactionStatusService transactionStatusService;

    public CreateAdjustmentTransactionCommandHandler(ITransactionService service, IManageAgencyService agencyService, IManageVCCTransactionTypeService transactionTypeService, IParameterizationService parameterizationService, IManageTransactionStatusService transactionStatusService) {
        this.service = service;
        this.agencyService = agencyService;
        this.transactionTypeService = transactionTypeService;
        this.parameterizationService = parameterizationService;
        this.transactionStatusService = transactionStatusService;
    }

    @Override
    public void handle(CreateAdjustmentTransactionCommand command) {
        RulesChecker.checkRule(new AdjustmentTransactionAmountRule(command.getAmount()));
        RulesChecker.checkRule(new AdjustmentTransactionReservationNumberRule(command.getReservationNumber()));

        ManageAgencyDto agencyDto = this.agencyService.findById(command.getAgency());

        ParameterizationDto parameterizationDto = this.parameterizationService.findActiveParameterization();
        if(Objects.isNull(parameterizationDto)){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", "No active parameterization")));
        }

        ManageTransactionStatusDto transactionStatusDto = this.transactionStatusService.findByCode(parameterizationDto.getTransactionStatusCode());
        ManageVCCTransactionTypeDto transactionCategory = this.transactionTypeService.findByCode(parameterizationDto.getTransactionCategory());
        ManageVCCTransactionTypeDto transactionSubCategory = this.transactionTypeService.findByCode(parameterizationDto.getTransactionSubCategory());

        Long id = this.service.create(new TransactionDto(
                agencyDto,
                transactionCategory,
                transactionSubCategory,
                command.getAmount(),
                command.getReservationNumber(),
                command.getReferenceNumber(),
                transactionStatusDto
        ));
        command.setId(id);
    }
}
