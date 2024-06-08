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
                DomainErrorMessage.MODULE_NAME_MUST_BY_UNIQUE, 
                new ErrorField("name", "The module name must be unique.")
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
