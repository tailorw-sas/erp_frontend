package com.kynsoft.finamer.settings.domain.rules.manageMerchantBankAccount;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantBankAccountService;

import java.util.UUID;

public class ManagerMerchantBankAccountMustBeUniqueByIdRule extends BusinessRule {

    private final IManageMerchantBankAccountService service;

    private final UUID managerMerchant;

    private final UUID manageBank;

    private final String accountNumber;

    private final UUID id;

    public ManagerMerchantBankAccountMustBeUniqueByIdRule(IManageMerchantBankAccountService service, UUID managerMerchant, UUID manageBank, UUID id, String accountNumber) {
        super(
                DomainErrorMessage.ITEM_ALREADY_EXITS, 
                new ErrorField("id", "Item already exists.")
        );
        this.service = service;
        this.managerMerchant = managerMerchant;
        this.manageBank = manageBank;
        this.id = id;
        this.accountNumber = accountNumber;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByManagerMerchantANDManagerCurrencyIdNotId(id, managerMerchant, manageBank, accountNumber) > 0;
    }

}
