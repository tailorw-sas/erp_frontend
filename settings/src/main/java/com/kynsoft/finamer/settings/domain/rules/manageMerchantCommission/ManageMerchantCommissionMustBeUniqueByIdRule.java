package com.kynsoft.finamer.settings.domain.rules.manageMerchantCommission;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantCommissionService;

import java.util.UUID;

public class ManageMerchantCommissionMustBeUniqueByIdRule extends BusinessRule {

    private final IManageMerchantCommissionService service;

    private final UUID managerMerchant;

    private final UUID manageCreditCartType;

    private final UUID id;

    public ManageMerchantCommissionMustBeUniqueByIdRule(IManageMerchantCommissionService service, UUID managerMerchant, UUID manageCreditCartType, UUID id) {
        super(
                DomainErrorMessage.MANAGER_MERCHANT_CURRENCY_MUST_BY_UNIQUE, 
                new ErrorField("id", "Item already exists.")
        );
        this.service = service;
        this.managerMerchant = managerMerchant;
        this.manageCreditCartType = manageCreditCartType;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByManagerMerchantANDManagerCreditCartTypeIdNotId(id, managerMerchant, manageCreditCartType) > 0;
    }

}
