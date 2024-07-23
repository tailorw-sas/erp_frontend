package com.kynsoft.finamer.invoicing.domain.rules.income;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import java.time.LocalDate;

public class CheckIfIncomeDateIsBeforeCurrentDateRule extends BusinessRule {

    private final LocalDate currentDate;
    private final LocalDate dateToValidate;

    public CheckIfIncomeDateIsBeforeCurrentDateRule(LocalDate dateToValidate) {
        super(DomainErrorMessage.INCOME_DATE_IS_BEFORE_CURRENT_DATE, new ErrorField("date", DomainErrorMessage.INCOME_DATE_IS_BEFORE_CURRENT_DATE.getReasonPhrase()));
        this.currentDate = LocalDate.now();
        this.dateToValidate = dateToValidate;
    }

    @Override
    public boolean isBroken() {
        return dateToValidate.isAfter(currentDate);
    }

}
