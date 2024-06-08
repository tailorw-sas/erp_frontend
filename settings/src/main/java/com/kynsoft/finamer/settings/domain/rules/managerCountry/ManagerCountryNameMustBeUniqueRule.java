package com.kynsoft.finamer.settings.domain.rules.managerCountry;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManagerCountryService;

import java.util.UUID;

public class ManagerCountryNameMustBeUniqueRule extends BusinessRule {

    private final IManagerCountryService service;

    private final String name;

    private final UUID id;

    public ManagerCountryNameMustBeUniqueRule(IManagerCountryService service, String name, UUID id) {
        super(
                DomainErrorMessage.MANAGER_COUNTRY_NAME_MUST_BY_UNIQUE, 
                new ErrorField("code", "The manager country name must be unique.")
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
