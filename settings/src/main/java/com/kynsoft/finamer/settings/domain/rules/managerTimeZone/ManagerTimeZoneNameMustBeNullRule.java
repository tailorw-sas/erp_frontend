package com.kynsoft.finamer.settings.domain.rules.managerTimeZone;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManagerTimeZoneNameMustBeNullRule extends BusinessRule {

    private final String name;

    public ManagerTimeZoneNameMustBeNullRule(String name) {
        super(
                DomainErrorMessage.MANAGER_TIME_ZONE_NAME_CANNOT_BE_EMPTY, 
                new ErrorField("name", "The name of the time zone cannot be empty.")
        );
        this.name = name;
    }

    @Override
    public boolean isBroken() {
        return this.name == null || this.name.isEmpty();
    }

}
