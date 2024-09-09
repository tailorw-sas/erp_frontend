package com.kynsoft.finamer.settings.domain.rules.managerMerchantConfig;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantConfigService;
import java.util.UUID;

public class ManagerMerchantConfigMustBeUniqueByRule extends BusinessRule {
    private final IManageMerchantConfigService service;
    private final UUID managerMerchant;
    private final UUID id;

    public ManagerMerchantConfigMustBeUniqueByRule(IManageMerchantConfigService service, UUID managerMerchant, UUID id) {
        super(
                DomainErrorMessage.MANAGER_MERCHANT_CONFIG_MUST_BY_UNIQUE,
                new ErrorField("id", "Item already exists.")
        );
        this.service = service;
        this.managerMerchant = managerMerchant;
        this.id = id;
    }
    @Override
    public boolean isBroken() {
        return this.service.countByManagerMerchantANDManagerCurrencyIdNotId(id, managerMerchant) > 0;
    }
}
