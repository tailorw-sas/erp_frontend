package com.kynsoft.finamer.settings.domain.rules.manageReportParamType;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageReportParamTypeService;

import java.util.UUID;

public class ManageReportParamTypeNameMustBeUniqueRule extends BusinessRule {

    private final IManageReportParamTypeService service;

    private final String name;

    private final UUID id;

    public ManageReportParamTypeNameMustBeUniqueRule(IManageReportParamTypeService service, String name, UUID id) {
        super(
                DomainErrorMessage.MANAGE_REPORT_PARAM_TYPE_NAME_MUST_BY_UNIQUE,
                new ErrorField("name", "The name must be unique.")
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
