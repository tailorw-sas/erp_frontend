package com.kynsoft.finamer.creditcard.application.command.manualTransaction.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.rules.manualTransaction.ManualTransactionAmountRule;
import com.kynsoft.finamer.creditcard.domain.rules.manualTransaction.ManualTransactionReservationNumberRule;
import com.kynsoft.finamer.creditcard.domain.rules.manualTransaction.ManualTransactionReservationNumberUniqueRule;
import com.kynsoft.finamer.creditcard.domain.services.*;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CreateManualTransactionCommandHandler implements ICommandHandler<CreateManualTransactionCommand> {

    private final ITransactionService service;

    private final IManageMerchantService merchantService;

    private final IManageHotelService hotelService;

    private final IManageAgencyService agencyService;

    private final IManageLanguageService languageService;

    private final IManageCreditCardTypeService creditCardTypeService;

    private final IManageTransactionStatusService transactionStatusService;

    private final IManageMerchantHotelEnrolleService merchantHotelEnrolleService;

    private final IParameterizationService parameterizationService;

    private final IManageVCCTransactionTypeService transactionTypeService;

    public CreateManualTransactionCommandHandler(ITransactionService service, IManageMerchantService merchantService, IManageHotelService hotelService, IManageAgencyService agencyService, IManageLanguageService languageService, IManageCreditCardTypeService creditCardTypeService, IManageTransactionStatusService transactionStatusService, IManageMerchantHotelEnrolleService merchantHotelEnrolleService, IParameterizationService parameterizationService, IManageVCCTransactionTypeService transactionTypeService) {
        this.service = service;
        this.merchantService = merchantService;
        this.hotelService = hotelService;
        this.agencyService = agencyService;
        this.languageService = languageService;
        this.creditCardTypeService = creditCardTypeService;
        this.transactionStatusService = transactionStatusService;
        this.merchantHotelEnrolleService = merchantHotelEnrolleService;
        this.parameterizationService = parameterizationService;
        this.transactionTypeService = transactionTypeService;
    }

    @Override
    public void handle(CreateManualTransactionCommand command) {
        RulesChecker.checkRule(new ManualTransactionAmountRule(command.getAmount()));
        RulesChecker.checkRule(new ManualTransactionReservationNumberRule(command.getReservationNumber()));

        ManageMerchantDto merchantDto = merchantService.findById(command.getMerchant());
        ManageHotelDto hotelDto = hotelService.findById(command.getHotel());

        RulesChecker.checkRule(new ManualTransactionReservationNumberUniqueRule(service, command.getReservationNumber(), command.getHotel()));

        ManageAgencyDto agencyDto = agencyService.findById(command.getAgency());
        ManageLanguageDto languageDto = languageService.findById(command.getLanguage());
        ManageCreditCardTypeDto creditCardTypeDto = null;

        ParameterizationDto parameterizationDto = parameterizationService.findActiveParameterization();
        if(Objects.isNull(parameterizationDto)){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", "No active parameterization")));
        }

        ManageTransactionStatusDto transactionStatusDto = transactionStatusService.findByCode(parameterizationDto.getTransactionStatusCode());
        ManageVCCTransactionTypeDto transactionCategory = this.transactionTypeService.findByCode(parameterizationDto.getTransactionCategory());
        ManageVCCTransactionTypeDto transactionSubCategory = this.transactionTypeService.findByCode(parameterizationDto.getTransactionSubCategory());

        if(command.getMethodType().name().equals("LINK")){
            RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getGuestName(), "gestName", "Guest name cannot be null."));
            RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getEmail(), "email", "Email cannot be null."));
        }

        ManageMerchantHotelEnrolleDto merchantHotelEnrolleDto = merchantHotelEnrolleService.findByManageMerchantAndManageHotel(
                merchantDto, hotelDto
        );

        double commission = 0;
        double netAmount = command.getAmount() - commission;

        Long id = this.service.create(new TransactionDto(
                merchantDto,
                command.getMethodType(),
                hotelDto,
                agencyDto,
                languageDto,
                command.getAmount(),
                command.getCheckIn(),
                command.getReservationNumber(),
                command.getReferenceNumber(),
                command.getHotelContactEmail(),
                command.getGuestName(),
                command.getEmail(),
                merchantHotelEnrolleDto.getEnrolle(),
                null,
                creditCardTypeDto,
                commission,
                transactionStatusDto,
                null,
                transactionCategory,
                transactionSubCategory,
                netAmount
        ));
        command.setId(id);
    }
}
