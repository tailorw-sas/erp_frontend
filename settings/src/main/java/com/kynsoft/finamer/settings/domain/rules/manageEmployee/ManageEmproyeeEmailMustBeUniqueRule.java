package com.kynsoft.finamer.settings.domain.rules.manageEmployee;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageEmployeeService;

import java.util.UUID;

public class ManageEmproyeeEmailMustBeUniqueRule extends BusinessRule {

    private final IManageEmployeeService service;

    private final String email;

    private final UUID id;

    public ManageEmproyeeEmailMustBeUniqueRule(IManageEmployeeService service, String email, UUID id) {
        super(
                DomainErrorMessage.MANAGE_EMPLOYEE_EMAIL_MUST_BY_UNIQUE, 
                new ErrorField("email", "The email must be unique.")
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
