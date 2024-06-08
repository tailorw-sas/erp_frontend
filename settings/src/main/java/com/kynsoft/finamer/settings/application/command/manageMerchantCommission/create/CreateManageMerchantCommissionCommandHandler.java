package com.kynsoft.finamer.settings.application.command.manageMerchantCommission.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.settings.domain.dto.ManageCreditCardTypeDto;
import com.kynsoft.finamer.settings.domain.dto.ManageMerchantCommissionDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantDto;
import com.kynsoft.finamer.settings.domain.rules.manageMerchantCommission.ManageMerchantCommissionFromDateMustBeNullRule;
import com.kynsoft.finamer.settings.domain.rules.manageMerchantCommission.ManageMerchantCommissionCommissionMustBeNullRule;
import com.kynsoft.finamer.settings.domain.rules.manageMerchantCommission.ManageMerchantCommissionFromDateAndToDateRule;
import com.kynsoft.finamer.settings.domain.rules.manageMerchantCommission.ManageMerchantCommissionMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.services.IManageCreditCardTypeService;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantCommissionService;
import com.kynsoft.finamer.settings.domain.services.IManagerMerchantService;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class CreateManageMerchantCommissionCommandHandler implements ICommandHandler<CreateManageMerchantCommissionCommand> {

    private final IManagerMerchantService serviceMerchantService;
    private final IManageCreditCardTypeService serviceCurrencyService;
    private final IManageMerchantCommissionService service;

    public CreateManageMerchantCommissionCommandHandler(IManagerMerchantService serviceMerchantService,
                                                       IManageCreditCardTypeService serviceCurrencyService,
                                                       IManageMerchantCommissionService service) {
        this.serviceMerchantService = serviceMerchantService;
        this.serviceCurrencyService = serviceCurrencyService;
        this.service = service;
    }

    @Override
    public void handle(CreateManageMerchantCommissionCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManagerMerchant(), "managerMerchant", "Manage Merchant ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManageCreditCartType(), "manageCreditCartType", "Manage Credit Cart Type ID cannot be null."));

        ManageCreditCardTypeDto manageCreditCardTypeDto = this.serviceCurrencyService.findById(command.getManageCreditCartType());
        ManagerMerchantDto managerMerchantDto = this.serviceMerchantService.findById(command.getManagerMerchant());

        LocalDate toDate = command.getToDate() != null ? command.getToDate() : LocalDate.parse("4000-12-31");
        RulesChecker.checkRule(new ManageMerchantCommissionFromDateMustBeNullRule(command.getFromDate()));
        RulesChecker.checkRule(new ManageMerchantCommissionCommissionMustBeNullRule(command.getCommission()));
        RulesChecker.checkRule(new ManageMerchantCommissionFromDateAndToDateRule(command.getFromDate(), toDate));

        RulesChecker.checkRule(new ManageMerchantCommissionMustBeUniqueRule(this.service, command.getId(), command.getManagerMerchant(), command.getManageCreditCartType(), command.getFromDate(), toDate));

        service.create(new ManageMerchantCommissionDto(
                command.getId(), 
                managerMerchantDto, 
                manageCreditCardTypeDto, 
                command.getCommission(), 
                command.getCalculationType(), 
                command.getDescription(), 
                command.getFromDate(), 
                toDate
        ));
    }
}
