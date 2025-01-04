package com.kynsoft.finamer.creditcard.domain.rules.managerAccountType;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.creditcard.domain.services.IManagerAccountTypeService;

import java.util.UUID;

public class ManagerAccountTypeCodeMustBeUniqueRule extends BusinessRule {

    private final IManagerAccountTypeService service;

    private final String code;

    private final UUID id;

    public ManagerAccountTypeCodeMustBeUniqueRule(IManagerAccountTypeService service, String code, UUID id) {
        super(
                DomainErrorMessage.ITEM_ALREADY_EXITS,
                new ErrorField("code", DomainErrorMessage.ITEM_ALREADY_EXITS.toString())
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
