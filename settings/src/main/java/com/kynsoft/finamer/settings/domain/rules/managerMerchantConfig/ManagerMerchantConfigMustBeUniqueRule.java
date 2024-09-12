package com.kynsoft.finamer.settings.domain.rules.managerMerchantConfig;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantConfigService;

import java.util.UUID;

public class ManagerMerchantConfigMustBeUniqueRule extends BusinessRule {

    private final IManageMerchantConfigService service;

    private final UUID managerMerchant;


    public ManagerMerchantConfigMustBeUniqueRule(IManageMerchantConfigService service, UUID managerMerchant ) {
        super(
                DomainErrorMessage.ITEM_ALREADY_EXITS,
                new ErrorField("id", DomainErrorMessage.ITEM_ALREADY_EXITS.getReasonPhrase())
        );
        this.service = service;
        this.managerMerchant = managerMerchant;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByCodeAndNotId(managerMerchant) > 0;
    }
}
