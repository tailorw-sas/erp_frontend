package com.kynsoft.finamer.settings.application.command.manageMerchantBankAccount.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.settings.domain.dto.ManageCreditCardTypeDto;
import com.kynsoft.finamer.settings.domain.dto.ManageMerchantBankAccountDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerBankDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantDto;
import com.kynsoft.finamer.settings.domain.rules.manageMerchantBankAccount.ManagerMerchantBankAccountMustBeUniqueByIdRule;
import com.kynsoft.finamer.settings.domain.services.IManageCreditCardTypeService;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantBankAccountService;
import com.kynsoft.finamer.settings.domain.services.IManagerBankService;
import com.kynsoft.finamer.settings.domain.services.IManagerMerchantService;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CreateManageMerchantBankAccountCommandHandler implements ICommandHandler<CreateManageMerchantBankAccountCommand> {

    private final IManagerMerchantService serviceMerchantService;
    private final IManagerBankService serviceBankService;
    private final IManageCreditCardTypeService serviceCreditCardTypeService;
    private final IManageMerchantBankAccountService serviceMerchantBankAccountService;

    public CreateManageMerchantBankAccountCommandHandler(IManagerMerchantService serviceMerchantService,
                                                       IManagerBankService serviceBankService,
                                                       IManageCreditCardTypeService serviceCreditCardTypeService,
                                                       IManageMerchantBankAccountService serviceMerchantBankAccountService) {
        this.serviceMerchantService = serviceMerchantService;
        this.serviceBankService = serviceBankService;
        this.serviceCreditCardTypeService = serviceCreditCardTypeService;
        this.serviceMerchantBankAccountService = serviceMerchantBankAccountService;
    }

    @Override
    public void handle(CreateManageMerchantBankAccountCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManagerBank(), "manageBank", "Manage Bank ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManagerMerchant(), "managerMerchant", "Manager Merchant ID cannot be null."));

        RulesChecker.checkRule(new ManagerMerchantBankAccountMustBeUniqueByIdRule(this.serviceMerchantBankAccountService, command.getManagerMerchant(), command.getManagerBank(), command.getId(), command.getAccountNumber()));

        ManagerBankDto managerBankDto = this.serviceBankService.findById(command.getManagerBank());
        ManagerMerchantDto managerMerchantDto = this.serviceMerchantService.findById(command.getManagerMerchant());

        Set<ManageCreditCardTypeDto> merchantCreditCardTypeDtos = new HashSet<>();

        for (UUID creditCardType : command.getCreditCardTypes()) {
            ManageCreditCardTypeDto entity = this.serviceCreditCardTypeService.findById(creditCardType);
            merchantCreditCardTypeDtos.add(entity);
        }
        serviceMerchantBankAccountService.create(new ManageMerchantBankAccountDto(
                command.getId(), 
                managerMerchantDto, 
                managerBankDto, 
                command.getAccountNumber(), 
                command.getDescription(),
                command.getStatus(),
                merchantCreditCardTypeDtos
        ));
    }
}
