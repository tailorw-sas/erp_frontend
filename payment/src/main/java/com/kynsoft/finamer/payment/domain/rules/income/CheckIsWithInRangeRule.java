package com.kynsoft.finamer.payment.domain.rules.income;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import java.time.LocalDate;

public class CheckIsWithInRangeRule extends BusinessRule {

    private final LocalDate currentDate;
    private final LocalDate beginDate;
    private final LocalDate endDate;

    public CheckIsWithInRangeRule(LocalDate currentDate, LocalDate beginDate, LocalDate endDate) {
        super(DomainErrorMessage.DATE_VALIDATE_CLOSE_OPERATION, new ErrorField("date", DomainErrorMessage.DATE_VALIDATE_CLOSE_OPERATION.getReasonPhrase()));
        this.currentDate = currentDate;
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    @Override
    public boolean isBroken() {
        Boolean bool = (currentDate.isEqual(beginDate) || currentDate.isEqual(endDate))
                       || (currentDate.isBefore(endDate) && currentDate.isAfter(beginDate));
        return !bool;
    }

}
