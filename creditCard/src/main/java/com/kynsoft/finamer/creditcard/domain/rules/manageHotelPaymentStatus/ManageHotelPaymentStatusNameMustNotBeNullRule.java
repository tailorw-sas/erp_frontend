package com.kynsoft.finamer.creditcard.domain.rules.manageHotelPaymentStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManageHotelPaymentStatusNameMustNotBeNullRule extends BusinessRule {

    private final String name;

    public ManageHotelPaymentStatusNameMustNotBeNullRule(String name) {
        super(
                DomainErrorMessage.NAME_CANNOT_BE_EMPTY,
                new ErrorField("name", DomainErrorMessage.NAME_CANNOT_BE_EMPTY.getReasonPhrase())
        );
        this.name = name;
    }

    @Override
    public boolean isBroken() {
        return this.name == null || this.name.isEmpty();
    }

}
