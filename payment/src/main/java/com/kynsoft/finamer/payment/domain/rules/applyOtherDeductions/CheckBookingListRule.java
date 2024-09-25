package com.kynsoft.finamer.payment.domain.rules.applyOtherDeductions;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import java.util.List;
import java.util.UUID;

public class CheckBookingListRule extends BusinessRule {

    private final List<UUID> bookings;

    public CheckBookingListRule(List<UUID> bookings) {
        super(DomainErrorMessage.BOOKING_LIST_CHECK, new ErrorField("bookings", DomainErrorMessage.BOOKING_LIST_CHECK.getReasonPhrase()));
        this.bookings = bookings;
    }

    /**
     * Verifica si la lista de booking posee mas de un elemento.
     * @return 
     */
    @Override
    public boolean isBroken() {
        return this.bookings == null;
    }

}
