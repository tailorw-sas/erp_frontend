package com.kynsoft.finamer.settings.domain.rules.managerPaymentStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManagePaymentStatusNameCantBeNullRule extends BusinessRule {

    private final String name;
    
    public ManagePaymentStatusNameCantBeNullRule(final String name) {
        super(DomainErrorMessage.MANAGER_PAYMENT_STATUS_NAME_CANT_BE_NULL, new ErrorField("name", "Name can't be empty"));
        this.name = name;
    }
    @Override
    public boolean isBroken() {
        return this.name == null || this.name.isEmpty();
    }
}
