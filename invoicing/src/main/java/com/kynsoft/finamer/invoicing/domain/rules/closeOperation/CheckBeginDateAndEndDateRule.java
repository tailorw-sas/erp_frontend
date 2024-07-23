package com.kynsoft.finamer.invoicing.domain.rules.closeOperation;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import java.time.LocalDate;

public class CheckBeginDateAndEndDateRule extends BusinessRule {

    private final LocalDate beginDate;
    private final LocalDate endDate;

    public CheckBeginDateAndEndDateRule(LocalDate beginDate, LocalDate endDate) {
        super(DomainErrorMessage.CHECK_DATES, new ErrorField("beginDate", DomainErrorMessage.CHECK_DATES.getReasonPhrase()));
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    @Override
    public boolean isBroken() {
        return beginDate.isAfter(endDate);
    }

}
