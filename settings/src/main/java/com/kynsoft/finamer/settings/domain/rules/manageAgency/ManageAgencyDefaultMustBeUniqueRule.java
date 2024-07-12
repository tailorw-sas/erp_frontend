package com.kynsoft.finamer.settings.domain.rules.manageAgency;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageAgencyService;

import java.util.UUID;

public class ManageAgencyDefaultMustBeUniqueRule extends BusinessRule {

    private final IManageAgencyService service;

    private final UUID id;

    public ManageAgencyDefaultMustBeUniqueRule(IManageAgencyService service, UUID id) {
        super(
                DomainErrorMessage.MANAGE_AGENCY_CHECK_DEFAULT,
                new ErrorField("default", DomainErrorMessage.MANAGE_AGENCY_CHECK_DEFAULT.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByDefaultAndNotId(id) > 0;
    }

}
