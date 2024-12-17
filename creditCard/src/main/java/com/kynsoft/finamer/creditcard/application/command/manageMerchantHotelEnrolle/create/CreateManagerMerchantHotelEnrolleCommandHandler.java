package com.kynsoft.finamer.creditcard.application.command.manageMerchantHotelEnrolle.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.creditcard.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantHotelEnrolleDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerCurrencyDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.rules.manageMechantHotelEnrolle.ManageMerchantHotelEnrolleEnrolleMustBeNullRule;
import com.kynsoft.finamer.creditcard.domain.rules.manageMechantHotelEnrolle.ManageMerchantHotelEnrolleKeyMustBeNullRule;
import com.kynsoft.finamer.creditcard.domain.rules.manageMechantHotelEnrolle.ManagerMerchantHotelEnrolleMustBeUniqueByIdRule;
import com.kynsoft.finamer.creditcard.domain.services.IManageHotelService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantHotelEnrolleService;
import com.kynsoft.finamer.creditcard.domain.services.IManagerCurrencyService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagerMerchantHotelEnrolleCommandHandler implements ICommandHandler<CreateManagerMerchantHotelEnrolleCommand> {

    private final IManageMerchantService serviceMerchantService;
    private final IManagerCurrencyService serviceCurrencyService;
    private final IManageHotelService serviceHotel;
    private final IManageMerchantHotelEnrolleService serviceMerchantHotelEnrolle;

    public CreateManagerMerchantHotelEnrolleCommandHandler(IManageMerchantService serviceMerchantService,
                                                           IManagerCurrencyService serviceCurrencyService,
                                                           IManageHotelService serviceHotel,
                                                           IManageMerchantHotelEnrolleService serviceMerchantHotelEnrolle) {
        this.serviceMerchantService = serviceMerchantService;
        this.serviceCurrencyService = serviceCurrencyService;
        this.serviceHotel = serviceHotel;
        this.serviceMerchantHotelEnrolle = serviceMerchantHotelEnrolle;
    }

    @Override
    public void handle(CreateManagerMerchantHotelEnrolleCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManagerCurrency(), "managerCurrency", "Manage Currency ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManagerMerchant(), "managerMerchant", "Manage Merchant ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManagerHotel(), "managerHotel", "Manage Hotel ID cannot be null."));

        RulesChecker.checkRule(new ManageMerchantHotelEnrolleKeyMustBeNullRule(command.getEnrolle()));
        RulesChecker.checkRule(new ManageMerchantHotelEnrolleEnrolleMustBeNullRule(command.getKey()));

        RulesChecker.checkRule(new ManagerMerchantHotelEnrolleMustBeUniqueByIdRule(this.serviceMerchantHotelEnrolle, command.getManagerMerchant(), command.getManagerCurrency(), command.getManagerHotel(), command.getEnrolle(), command.getId()));
        ManagerCurrencyDto managerCurrencyDto = this.serviceCurrencyService.findById(command.getManagerCurrency());
        ManageMerchantDto managerMerchantDto = this.serviceMerchantService.findById(command.getManagerMerchant());
        ManageHotelDto manageHotelDto = this.serviceHotel.findById(command.getManagerHotel());

        serviceMerchantHotelEnrolle.create(new ManageMerchantHotelEnrolleDto(
                                                command.getId(), 
                                                managerMerchantDto, 
                                                managerCurrencyDto, 
                                                manageHotelDto, 
                                                command.getEnrolle(), 
                                                command.getKey(), 
                                                command.getDescription(),
                                                command.getStatus()
                                        ));
    }
}
