package com.kynsoft.finamer.settings.domain.rules.manageMerchantCommission;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.time.LocalDate;

public class ManageMerchantCommissionHasOverlappingRecords extends BusinessRule {

    private final LocalDate fromCheckDate;

    private final LocalDate toCheckDate;

    public ManageMerchantCommissionHasOverlappingRecords(LocalDate fromCheckDate, LocalDate toCheckDate) {
        super(
                DomainErrorMessage.MANAGER_MERCHANT_CURRENCY_MUST_BY_UNIQUE, 
                new ErrorField("fromDate", "Start date should not be greater than end date.")
        );
        this.fromCheckDate = fromCheckDate;
        this.toCheckDate = toCheckDate;
    }

    @Override
    public boolean isBroken() {
        return !toCheckDate.isAfter(fromCheckDate);
    }

}
