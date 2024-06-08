package com.kynsof.identity.domain.rules.usersystem;

import com.kynsof.identity.domain.interfaces.service.IUserSystemService;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.util.UUID;

public class ModuleUserNameMustBeUniqueRule extends BusinessRule {

    private final IUserSystemService service;

    private final String username;

    private final UUID id;

    public ModuleUserNameMustBeUniqueRule(IUserSystemService service, String username, UUID id) {
        super(
                DomainErrorMessage.MODULE_NAME_MUST_BY_UNIQUE, 
                new ErrorField("username", "The user username must be unique.")
        );
        this.service = service;
        this.username = username;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByUserNameAndNotId(username, id) > 0;
    }

}
