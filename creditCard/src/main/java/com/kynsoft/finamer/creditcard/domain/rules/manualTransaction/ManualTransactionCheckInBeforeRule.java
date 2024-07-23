package com.kynsoft.finamer.creditcard.domain.rules.manualTransaction;


import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.time.LocalDate;

public class ManualTransactionCheckInBeforeRule extends BusinessRule {

    private final LocalDate currentDate;

    private final LocalDate checkInDate;

    public ManualTransactionCheckInBeforeRule(LocalDate checkInDate) {
        super(
                DomainErrorMessage.VCC_CHECKIN_DATE_IS_BEFORE_CURRENT_DATE,
                new ErrorField("checkIn", DomainErrorMessage.VCC_CHECKIN_DATE_IS_BEFORE_CURRENT_DATE.getReasonPhrase())
        );
        this.currentDate = LocalDate.now();
        this.checkInDate = checkInDate;
    }

    @Override
    public boolean isBroken() {
        return checkInDate.isAfter(currentDate);
    }
}
