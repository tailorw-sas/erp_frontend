package com.kynsoft.finamer.settings.domain.rules.manageMechantHotelEnrolle;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManageMerchantHotelEnrolleKeyMustBeNullRule extends BusinessRule {

    private final String key;

    public ManageMerchantHotelEnrolleKeyMustBeNullRule(String key) {
        super(
                DomainErrorMessage.FIELD_IS_REQUIRED, 
                new ErrorField("key", "The key field is required.")
        );
        this.key = key;
    }

    @Override
    public boolean isBroken() {
        return this.key == null || this.key.isEmpty();
    }

}
