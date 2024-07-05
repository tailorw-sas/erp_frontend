package com.kynsof.identity.domain.rules.usersystem;

import com.kynsof.identity.domain.interfaces.service.IUserSystemService;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.util.UUID;

public class ModuleEmailMustBeUniqueRule extends BusinessRule {

    private final IUserSystemService service;

    private final String email;

    private final UUID id;

    public ModuleEmailMustBeUniqueRule(IUserSystemService service, String email, UUID id) {
        super(
                DomainErrorMessage.EMAIL_ALREADY_EXISTS,
                new ErrorField("email", DomainErrorMessage.EMAIL_ALREADY_EXISTS.getReasonPhrase())
        );
        this.service = service;
        this.email = email;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByEmailAndNotId(email, id) > 0;
    }

}
