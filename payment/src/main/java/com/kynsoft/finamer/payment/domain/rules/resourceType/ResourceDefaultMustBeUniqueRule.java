package com.kynsoft.finamer.payment.domain.rules.resourceType;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.payment.domain.services.IManageResourceTypeService;

import java.util.UUID;

public class ResourceDefaultMustBeUniqueRule extends BusinessRule {

    private final IManageResourceTypeService service;

    private final UUID id;

    public ResourceDefaultMustBeUniqueRule(IManageResourceTypeService service, UUID id) {
        super(
                DomainErrorMessage.MANAGE_PAYMENT_TRANSACTION_TYPE_CHECK_DEFAULT,
                new ErrorField("code", DomainErrorMessage.MANAGE_PAYMENT_TRANSACTION_TYPE_CHECK_DEFAULT.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByDefaultAndNotId(id) > 0;
    }

}
