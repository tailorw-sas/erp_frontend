package com.kynsoft.finamer.payment.domain.rules.payment;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.time.LocalDate;

public class CheckIfTransactionDateIsBeforeCurrentDateRule extends BusinessRule {

    private final LocalDate currentDate;
    private final LocalDate dateToValidate;

    public CheckIfTransactionDateIsBeforeCurrentDateRule(LocalDate dateToValidate) {
        super(DomainErrorMessage.PAYMENT_TRANSACTION_DATE_IS_BEFORE_CURRENT_DATE, new ErrorField("date", DomainErrorMessage.PAYMENT_TRANSACTION_DATE_IS_BEFORE_CURRENT_DATE.getReasonPhrase()));
        this.currentDate = LocalDate.now();
        this.dateToValidate = dateToValidate;
    }

    @Override
    public boolean isBroken() {
        return dateToValidate.isAfter(currentDate);
    }

}
