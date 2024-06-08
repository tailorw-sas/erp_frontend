package com.kynsoft.finamer.settings.domain.rules.managerMerchantCurrency;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManagerMerchantCurrencyService;

import java.util.UUID;

public class ManagerMerchantCurrencyMustBeUniqueRule extends BusinessRule {

    private final IManagerMerchantCurrencyService service;

    private final UUID managerMerchant;

    private final UUID managerCurrency;

    public ManagerMerchantCurrencyMustBeUniqueRule(IManagerMerchantCurrencyService service, UUID managerMerchant, UUID managerCurrency) {
        super(
                DomainErrorMessage.MANAGER_MERCHANT_CURRENCY_MUST_BY_UNIQUE, 
                new ErrorField("id", "Item already exists.")
        );
        this.service = service;
        this.managerMerchant = managerMerchant;
        this.managerCurrency = managerCurrency;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByCodeAndNotId(managerMerchant, managerCurrency) > 0;
    }

}
