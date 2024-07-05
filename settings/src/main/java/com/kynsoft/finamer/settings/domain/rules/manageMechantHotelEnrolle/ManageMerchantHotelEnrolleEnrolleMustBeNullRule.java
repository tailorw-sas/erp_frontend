package com.kynsoft.finamer.settings.domain.rules.manageMechantHotelEnrolle;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManageMerchantHotelEnrolleEnrolleMustBeNullRule extends BusinessRule {

    private final String enrolle;

    public ManageMerchantHotelEnrolleEnrolleMustBeNullRule(String enrolle) {
        super(
                DomainErrorMessage.FIELD_IS_REQUIRED, 
                new ErrorField("enrolle", "The enrolle field is required.")
        );
        this.enrolle = enrolle;
    }

    @Override
    public boolean isBroken() {
        return this.enrolle == null || this.enrolle.isEmpty();
    }

}
