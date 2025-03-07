package com.kynsoft.finamer.insis.domain.rules.booking;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ImportBookingSizeRule extends BusinessRule {

    private final int originalBookingsSize;
    private final int currentBookingsSize;

    public ImportBookingSizeRule(Integer originalBookingsSize,
                                 Integer currentBookingsSize){
        super(
                DomainErrorMessage.INNSIST_IMPORT_BOOKING_SIZE_RULE,
                new ErrorField("BookingsSize", DomainErrorMessage.INNSIST_IMPORT_BOOKING_SIZE_RULE.getReasonPhrase())
        );
        this.originalBookingsSize = originalBookingsSize;
        this.currentBookingsSize = currentBookingsSize;
    }

    @Override
    public boolean isBroken() {
        return originalBookingsSize != currentBookingsSize;
    }
}
