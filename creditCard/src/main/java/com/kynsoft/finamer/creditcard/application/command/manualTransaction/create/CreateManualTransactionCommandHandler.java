package com.kynsoft.finamer.creditcard.application.command.manualTransaction.create;

import com.kynsof.share.core.application.mailjet.MailJetRecipient;
import com.kynsof.share.core.application.mailjet.MailJetVar;
import com.kynsof.share.core.application.mailjet.MailService;
import com.kynsof.share.core.application.mailjet.SendMailJetEMailRequest;
import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.MethodType;
import com.kynsoft.finamer.creditcard.domain.rules.manualTransaction.*;
import com.kynsoft.finamer.creditcard.domain.services.*;
import com.kynsoft.finamer.creditcard.infrastructure.services.TokenService;
import org.springframework.stereotype.Component;

import java.util.*;

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

    private final ICreditCardCloseOperationService closeOperationService;

    private final TokenService tokenService;

    private final MailService mailService;

    public CreateManualTransactionCommandHandler(ITransactionService service, IManageMerchantService merchantService, IManageHotelService hotelService, IManageAgencyService agencyService, IManageLanguageService languageService, IManageCreditCardTypeService creditCardTypeService, IManageTransactionStatusService transactionStatusService, IManageMerchantHotelEnrolleService merchantHotelEnrolleService, IParameterizationService parameterizationService, IManageVCCTransactionTypeService transactionTypeService, ICreditCardCloseOperationService closeOperationService, TokenService tokenService, MailService mailService) {
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
        this.closeOperationService = closeOperationService;
        this.tokenService = tokenService;
        this.mailService = mailService;
    }

    @Override
    public void handle(CreateManualTransactionCommand command) {
        RulesChecker.checkRule(new ManualTransactionAmountRule(command.getAmount()));
        RulesChecker.checkRule(new ManualTransactionCheckInBeforeRule(command.getCheckIn()));
        RulesChecker.checkRule(new ManualTransactionCheckInCloseOperationRule(this.closeOperationService, command.getCheckIn(), command.getHotel()));

        ManageMerchantDto merchantDto = this.merchantService.findById(command.getMerchant());
        ManageHotelDto hotelDto = this.hotelService.findById(command.getHotel());

        RulesChecker.checkRule(new ManualTransactionReservationNumberUniqueRule(this.service, command.getReservationNumber(), command.getHotel()));

        ManageAgencyDto agencyDto = this.agencyService.findById(command.getAgency());
        ManageLanguageDto languageDto = this.languageService.findById(command.getLanguage());

//        RulesChecker.checkRule(new ManualTransactionAgencyBookingFormatRule(agencyDto.getBookingCouponFormat()));
//        RulesChecker.checkRule(new ManualTransactionReservationNumberRule(command.getReservationNumber(), agencyDto.getBookingCouponFormat()));

        ManageCreditCardTypeDto creditCardTypeDto = null;

        ParameterizationDto parameterizationDto = this.parameterizationService.findActiveParameterization();
        if(Objects.isNull(parameterizationDto)){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", "No active parameterization")));
        }

        ManageTransactionStatusDto transactionStatusDto = this.transactionStatusService.findByCode(parameterizationDto.getTransactionStatusCode());
        ManageVCCTransactionTypeDto transactionCategory = this.transactionTypeService.findByCode(parameterizationDto.getTransactionCategory());
        ManageVCCTransactionTypeDto transactionSubCategory = this.transactionTypeService.findByCode(parameterizationDto.getTransactionSubCategory());

        if(command.getMethodType().compareTo(MethodType.LINK) == 0){
            RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getGuestName(), "gestName", "Guest name cannot be null."));
            RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getEmail(), "email", "Email cannot be null."));
        }

        ManageMerchantHotelEnrolleDto merchantHotelEnrolleDto = this.merchantHotelEnrolleService.findByManageMerchantAndManageHotel(
                merchantDto, hotelDto
        );

        double commission = 0;
        double netAmount = command.getAmount() - commission;

        Long id = this.service.create(new TransactionDto(
                command.getTransactionUuid(),
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
                netAmount,
                true
        ));
        command.setId(id);

        //Send mail after the crate transaction
        String token = tokenService.generateToken(command.getTransactionUuid());
        if (command.getEmail() != null) {
            SendMailJetEMailRequest request = new SendMailJetEMailRequest();
            request.setTemplateId(6324713); // Cambiar en configuración

            // Variables para el template de email
            List<MailJetVar> vars = Arrays.asList(
                    new MailJetVar("payment_link", "http://localhost:3000/" + "?param=" + "payment?token=" + "&var=" + token),
                    new MailJetVar("invoice_amount", command.getAmount().toString())
            );
            request.setMailJetVars(vars);

            // Recipients
            List<MailJetRecipient> recipients = new ArrayList<>();
            recipients.add(new MailJetRecipient(command.getEmail(), command.getGuestName()));
            request.setRecipientEmail(recipients);

            mailService.sendMail(request);
            }
    }

}
