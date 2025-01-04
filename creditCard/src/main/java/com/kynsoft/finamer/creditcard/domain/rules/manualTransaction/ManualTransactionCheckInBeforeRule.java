package com.kynsoft.finamer.creditcard.domain.rules.manualTransaction;


import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ManualTransactionCheckInBeforeRule extends BusinessRule {

    private final LocalDate currentDate;

    private final LocalDateTime checkInDate;

    public ManualTransactionCheckInBeforeRule(LocalDateTime checkInDate) {
        super(
                DomainErrorMessage.VCC_CHECKIN_DATE_IS_BEFORE_CURRENT_DATE,
                new ErrorField("checkIn", DomainErrorMessage.VCC_CHECKIN_DATE_IS_BEFORE_CURRENT_DATE.getReasonPhrase())
        );
        this.currentDate = LocalDate.now();
        this.checkInDate = checkInDate;
    }

    @Override
    public boolean isBroken() {
        return checkInDate.toLocalDate().isAfter(currentDate);
    }
}
