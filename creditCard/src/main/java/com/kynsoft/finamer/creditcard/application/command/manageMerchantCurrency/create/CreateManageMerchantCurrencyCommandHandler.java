package com.kynsoft.finamer.creditcard.application.command.manageMerchantCurrency.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerCurrencyDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantCurrencyDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.services.IManagerCurrencyService;
import com.kynsoft.finamer.creditcard.domain.services.IManagerMerchantCurrencyService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageMerchantCurrencyCommandHandler implements ICommandHandler<CreateManageMerchantCurrencyCommand> {

    private final IManageMerchantService serviceMerchantService;
    private final IManagerCurrencyService serviceCurrencyService;
    private final IManagerMerchantCurrencyService serviceMerchantCurrency;

    public CreateManageMerchantCurrencyCommandHandler(IManageMerchantService serviceMerchantService,
                                                      IManagerCurrencyService serviceCurrencyService,
                                                      IManagerMerchantCurrencyService serviceMerchantCurrency) {
        this.serviceMerchantService = serviceMerchantService;
        this.serviceCurrencyService = serviceCurrencyService;
        this.serviceMerchantCurrency = serviceMerchantCurrency;
    }

    @Override
    public void handle(CreateManageMerchantCurrencyCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManagerCurrency(), "id", "Manager Currency ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManagerMerchant(), "id", "Manager Merchant ID cannot be null."));

        ManagerCurrencyDto managerCurrencyDto = this.serviceCurrencyService.findById(command.getManagerCurrency());
        ManageMerchantDto managerMerchantDto = this.serviceMerchantService.findById(command.getManagerMerchant());

        serviceMerchantCurrency.create(new ManagerMerchantCurrencyDto(command.getId(), managerMerchantDto, managerCurrencyDto, command.getValue(), command.getDescription(), command.getStatus()));
    }
}
