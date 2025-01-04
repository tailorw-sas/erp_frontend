package com.kynsoft.finamer.payment.domain.rules.resourceType;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.payment.domain.services.IManageResourceTypeService;

import java.util.UUID;

public class ResourceVccMustBeUniqueRule extends BusinessRule {

    private final IManageResourceTypeService service;

    private final UUID id;

    public ResourceVccMustBeUniqueRule(IManageResourceTypeService service, UUID id) {
        super(
                DomainErrorMessage.MANAGE_PAYMENT_RESOURCE_TYPE_CHECK_VCC,
                new ErrorField("vcc", DomainErrorMessage.MANAGE_PAYMENT_RESOURCE_TYPE_CHECK_VCC.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByVccAndNotId(id) > 0;
    }

}
