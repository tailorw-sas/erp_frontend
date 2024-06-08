package com.kynsoft.finamer.settings.domain.rules.manageMerchantCommission;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import java.time.LocalDate;

public class ManageMerchantCommissionFromDateMustBeNullRule extends BusinessRule {

    private final LocalDate fromDate;

    public ManageMerchantCommissionFromDateMustBeNullRule(LocalDate fromDate) {
        super(
                DomainErrorMessage.MANAGER_MERCHANT_COMMISSION_FROM_DATE_CANNOT_BE_EMPTY, 
                new ErrorField("name", "The fromDate cannot be empty.")
        );
        this.fromDate = fromDate;
    }

    @Override
    public boolean isBroken() {
        return this.fromDate == null;
    }

}
