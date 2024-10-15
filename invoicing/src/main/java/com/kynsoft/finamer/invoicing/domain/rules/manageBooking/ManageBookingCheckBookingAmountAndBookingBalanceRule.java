package com.kynsoft.finamer.invoicing.domain.rules.manageBooking;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import java.util.Objects;

public class ManageBookingCheckBookingAmountAndBookingBalanceRule extends BusinessRule {

    private final Double invoiceAmount;
    private final Double dueAmount;

    public ManageBookingCheckBookingAmountAndBookingBalanceRule(Double invoiceAmount, Double dueAmount) {
        super(
                DomainErrorMessage.MANAGE_EDIT_BOOKING_AMOUNT_AND_BOOKING_BALANCE, 
                new ErrorField("bookingAmount", DomainErrorMessage.MANAGE_EDIT_BOOKING_AMOUNT_AND_BOOKING_BALANCE.getReasonPhrase())
        );
        this.invoiceAmount = invoiceAmount;
        this.dueAmount = dueAmount;
    }

    @Override
    public boolean isBroken() {
        return !Objects.equals(invoiceAmount, dueAmount);
    }

}
