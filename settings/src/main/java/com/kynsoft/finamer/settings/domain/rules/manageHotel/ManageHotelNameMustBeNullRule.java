package com.kynsoft.finamer.settings.domain.rules.manageHotel;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManageHotelNameMustBeNullRule extends BusinessRule {

    private final String name;

    public ManageHotelNameMustBeNullRule(String name) {
        super(
                DomainErrorMessage.MANAGE_HOTEL_NAME_CANNOT_BE_EMPTY,
                new ErrorField("name", "The name cannot be empty.")
        );
        this.name = name;
    }

    @Override
    public boolean isBroken() {
        return this.name == null || this.name.isEmpty();
    }
}
