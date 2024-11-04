package com.kynsoft.finamer.creditcard.application.command.manageMerchantBankAccount.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.creditcard.domain.dto.ManageCreditCardTypeDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantBankAccountDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerBankDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import com.kynsoft.finamer.creditcard.domain.rules.manageMerchantBankAccount.ManagerMerchantBankAccountMustBeUniqueByIdRule;
import com.kynsoft.finamer.creditcard.domain.services.IManageCreditCardTypeService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantBankAccountService;
import com.kynsoft.finamer.creditcard.domain.services.IManagerBankService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@Component
public class UpdateManageMerchantBankAccountCommandHandler implements ICommandHandler<UpdateManageMerchantBankAccountCommand> {

    private final IManageMerchantService serviceMerchantService;
    private final IManagerBankService serviceBankService;
    private final IManageCreditCardTypeService serviceCreditCardTypeService;
    private final IManageMerchantBankAccountService serviceMerchantBankAccountService;

    public UpdateManageMerchantBankAccountCommandHandler(IManageMerchantService serviceMerchantService,
                                                         IManagerBankService serviceBankService,
                                                         IManageCreditCardTypeService serviceCreditCardTypeService,
                                                         IManageMerchantBankAccountService serviceMerchantBankAccountService) {
        this.serviceMerchantService = serviceMerchantService;
        this.serviceBankService = serviceBankService;
        this.serviceCreditCardTypeService = serviceCreditCardTypeService;
        this.serviceMerchantBankAccountService = serviceMerchantBankAccountService;
    }

    @Override
    public void handle(UpdateManageMerchantBankAccountCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Merchant Bank Account ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManagerBank(), "manageBank", "Manage Bank ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getManagerMerchant(), "manageMerchant", "Manager Merchant ID cannot be null."));

        Set<ManageMerchantDto> manageMerchantDtoSet = new HashSet<>();
        for (UUID merchantId  : command.getManagerMerchant()){
            RulesChecker.checkRule(new ManagerMerchantBankAccountMustBeUniqueByIdRule(this.serviceMerchantBankAccountService, merchantId, command.getManagerBank(), command.getId(), command.getAccountNumber()));
            ManageMerchantDto merchantDto = this.serviceMerchantService.findById(merchantId);
            manageMerchantDtoSet.add(merchantDto);
        }
        ManageMerchantBankAccountDto test = this.serviceMerchantBankAccountService.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();
        this.updateManageBank(test::setManageBank, command.getManagerBank(), test.getManageBank().getId(), update::setUpdate);

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setAccountNumber, command.getAccountNumber(), test.getAccountNumber(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setDescription, command.getDescription(), test.getDescription(), update::setUpdate);
        this.updateStatus(test::setStatus, command.getStatus(), test.getStatus(), update::setUpdate);

        Set<ManageCreditCardTypeDto> merchantCreditCardTypeDtos = new HashSet<>();

        for (UUID creditCardType : command.getCreditCardTypes()) {
            ManageCreditCardTypeDto entity = this.serviceCreditCardTypeService.findById(creditCardType);
            merchantCreditCardTypeDtos.add(entity);
        }
        test.getCreditCardTypes().clear();
        test.setCreditCardTypes(merchantCreditCardTypeDtos);
        test.getManagerMerchant().clear();
        test.setManagerMerchant(manageMerchantDtoSet);
        serviceMerchantBankAccountService.update(test);
    }
    
    private boolean updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateManageBank(Consumer<ManagerBankDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManagerBankDto managerBankDto = this.serviceBankService.findById(newValue);
            setter.accept(managerBankDto);
            update.accept(1);

            return true;
        }
        return false;
    }
}
