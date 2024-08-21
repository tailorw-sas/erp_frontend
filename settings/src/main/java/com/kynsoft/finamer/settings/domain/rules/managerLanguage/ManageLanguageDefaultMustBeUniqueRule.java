package com.kynsoft.finamer.settings.domain.rules.managerLanguage;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManagerLanguageService;

import java.util.UUID;

public class ManageLanguageDefaultMustBeUniqueRule extends BusinessRule {

    private final IManagerLanguageService service;

    private final UUID id;

    public ManageLanguageDefaultMustBeUniqueRule(IManagerLanguageService service, UUID id) {
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
