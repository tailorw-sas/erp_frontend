package com.kynsoft.finamer.invoicing.domain.rules.resourceType;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.invoicing.domain.services.IManageResourceTypeService;

import java.util.UUID;

public class ResourceTypeDefaultsMustBeUniqueRule extends BusinessRule {

    private final IManageResourceTypeService service;

    private final UUID id;

    public ResourceTypeDefaultsMustBeUniqueRule(IManageResourceTypeService service, UUID id) {
        super(
                DomainErrorMessage.MANAGE_RESOURCE_TYPE_DEFAULT,
                new ErrorField("defaults", DomainErrorMessage.MANAGE_RESOURCE_TYPE_DEFAULT.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByDefaultsAndNotId(id) > 0;
    }

}
