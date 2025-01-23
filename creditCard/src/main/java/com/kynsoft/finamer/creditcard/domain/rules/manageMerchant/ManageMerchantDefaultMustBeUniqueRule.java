package com.kynsoft.finamer.creditcard.domain.rules.manageMerchant;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;

import java.util.UUID;

public class ManageMerchantDefaultMustBeUniqueRule extends BusinessRule {

    private final IManageMerchantService service;

    private final UUID id;

    public ManageMerchantDefaultMustBeUniqueRule(IManageMerchantService service, UUID id) {
        super(
                DomainErrorMessage.MANAGE_MERCHANT_DEFAULT,
                new ErrorField("default", DomainErrorMessage.MANAGE_MERCHANT_DEFAULT.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByDefaultAndNotId(id) > 0;
    }

}
