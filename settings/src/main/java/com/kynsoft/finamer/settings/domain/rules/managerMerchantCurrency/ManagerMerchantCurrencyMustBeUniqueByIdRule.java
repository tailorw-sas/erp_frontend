package com.kynsoft.finamer.settings.domain.rules.managerMerchantCurrency;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManagerMerchantCurrencyService;

import java.util.UUID;

public class ManagerMerchantCurrencyMustBeUniqueByIdRule extends BusinessRule {

    private final IManagerMerchantCurrencyService service;

    private final UUID managerMerchant;

    private final UUID managerCurrency;

    private final UUID id;

    public ManagerMerchantCurrencyMustBeUniqueByIdRule(IManagerMerchantCurrencyService service, UUID managerMerchant, UUID managerCurrency, UUID id) {
        super(
                DomainErrorMessage.MANAGER_MERCHANT_CURRENCY_MUST_BY_UNIQUE, 
                new ErrorField("id", "Item already exists.")
        );
        this.service = service;
        this.managerMerchant = managerMerchant;
        this.managerCurrency = managerCurrency;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByManagerMerchantANDManagerCurrencyIdNotId(id, managerMerchant, managerCurrency) > 0;
    }

}
