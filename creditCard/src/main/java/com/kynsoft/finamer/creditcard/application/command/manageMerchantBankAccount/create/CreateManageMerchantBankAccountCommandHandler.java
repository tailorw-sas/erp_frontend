package com.kynsoft.finamer.creditcard.application.command.manageMerchantBankAccount.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.creditcard.domain.dto.ManageCreditCardTypeDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantBankAccountDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerBankDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.rules.manageMerchantBankAccount.ManagerMerchantBankAccountMustBeUniqueByIdRule;
import com.kynsoft.finamer.creditcard.domain.services.IManageCreditCardTypeService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantBankAccountService;
import com.kynsoft.finamer.creditcard.domain.services.IManagerBankService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class CreateManageMerchantBankAccountCommandHandler implements ICommandHandler<CreateManageMerchantBankAccountCommand> {

    private final IManageMerchantService serviceMerchantService;
    private final IManagerBankService serviceBankService;
    private final IManageCreditCardTypeService serviceCreditCardTypeService;
    private final IManageMerchantBankAccountService serviceMerchantBankAccountService;

    public CreateManageMerchantBankAccountCommandHandler(IManageMerchantService serviceMerchantService,
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
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManagerMerchant(), "manageMerchant", "Manager Merchant ID cannot be null."));

        RulesChecker.checkRule(new ManagerMerchantBankAccountMustBeUniqueByIdRule(this.serviceMerchantBankAccountService, command.getManagerMerchant(), command.getManagerBank(), command.getId(), command.getAccountNumber()));

        ManagerBankDto managerBankDto = this.serviceBankService.findById(command.getManagerBank());
        ManageMerchantDto managerMerchantDto = this.serviceMerchantService.findById(command.getManagerMerchant());

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
