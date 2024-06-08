package com.kynsoft.finamer.settings.domain.rules.manageMerchantCommission;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManageMerchantCommissionCommissionMustBeNullRule extends BusinessRule {

    private final Double commission;

    public ManageMerchantCommissionCommissionMustBeNullRule(Double commission) {
        super(
                DomainErrorMessage.MANAGER_MERCHANT_COMMISSION_COMMISSION_CANNOT_BE_EMPTY, 
                new ErrorField("name", "The commission cannot be empty.")
        );
        this.commission = commission;
    }

    @Override
    public boolean isBroken() {
        return this.commission == null;
    }

}
