package com.kynsof.identity.domain.rules.module;

import com.kynsof.identity.domain.interfaces.service.IModuleService;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.util.UUID;

public class ModuleNameMustBeUniqueRule extends BusinessRule {

    private final IModuleService service;

    private final String name;

    private final UUID id;

    public ModuleNameMustBeUniqueRule(IModuleService service, String name, UUID id) {
        super(
                DomainErrorMessage.ITEM_ALREADY_EXITS,
                new ErrorField("name", DomainErrorMessage.ITEM_ALREADY_EXITS.getReasonPhrase())
        );
        this.service = service;
        this.name = name;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByNameAndNotId(name, id) > 0;
    }

}
