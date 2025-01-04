package com.kynsoft.finamer.invoicing.domain.rules.managerCountry;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManagerCountryNameMustBeNullRule extends BusinessRule {

    private final String name;

    public ManagerCountryNameMustBeNullRule(String name) {
        super(
                DomainErrorMessage.MANAGER_COUNTRY_NAME_CANNOT_BE_EMPTY, 
                new ErrorField("name", "The name of the country cannot be empty.")
        );
        this.name = name;
    }

    @Override
    public boolean isBroken() {
        return this.name == null || this.name.isEmpty();
    }

}
