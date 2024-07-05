package com.kynsoft.finamer.settings.domain.rules.manageEmployee;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageEmployeeService;

import java.util.UUID;

public class ManageEmproyeeLoginNameMustBeUniqueRule extends BusinessRule {

    private final IManageEmployeeService service;

    private final String loginName;

    private final UUID id;

    public ManageEmproyeeLoginNameMustBeUniqueRule(IManageEmployeeService service, String loginName, UUID id) {
        super(
                DomainErrorMessage.ITEM_ALREADY_EXITS,
                new ErrorField("loginName", DomainErrorMessage.ITEM_ALREADY_EXITS.getReasonPhrase())
        );
        this.service = service;
        this.loginName = loginName;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByLoginNameAndNotId(loginName, id) > 0;
    }

}
