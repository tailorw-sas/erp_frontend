package com.kynsoft.finamer.settings.domain.rules.manageDepartmentGroup;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageDepartmentGroupService;

import java.util.UUID;

public class ManageDepartmentGroupCodeMustBeUniqueRule extends BusinessRule {

    private final IManageDepartmentGroupService service;

    private final String code;

    private final UUID id;

    public ManageDepartmentGroupCodeMustBeUniqueRule(IManageDepartmentGroupService service,
            String code, UUID id) {
        super(
                DomainErrorMessage.ITEM_ALREADY_EXITS,
                new ErrorField("code", DomainErrorMessage.ITEM_ALREADY_EXITS.getReasonPhrase())
        );
        this.service = service;
        this.code = code;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByCodeAndNotId(code, id) > 0;
    }

}
