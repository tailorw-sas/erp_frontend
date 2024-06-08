package com.kynsof.identity.domain.rules.permission;

import com.kynsof.identity.domain.interfaces.service.IPermissionService;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.util.UUID;

public class PermissionCodeMustBeUniqueRule extends BusinessRule {

    private final IPermissionService service;

    private final String code;

    private final UUID id;

    public PermissionCodeMustBeUniqueRule(IPermissionService service, String code, UUID id) {
        super(
                DomainErrorMessage.PERMISSION_CODE_MUST_BY_UNIQUE, 
                new ErrorField("code", "The permission code must be unique.")
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
