package com.kynsoft.finamer.payment.domain.rules.paymentDetail;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentSourceDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;

public class CheckAmountGreaterThanZeroStrictlyAndLessBookingBalanceRule extends BusinessRule {

    private final ManagePaymentSourceDto paymentSourceDto;
    private final boolean isFromCredit;
    private final Double amount;
    private final Double bookingBalance;

    public CheckAmountGreaterThanZeroStrictlyAndLessBookingBalanceRule(ManagePaymentSourceDto paymentSourceDto, boolean isFromCredit, Double amount, Double bookingBalance) {
        super(DomainErrorMessage.BOOKING_CHECK_AMOUNT_GREATER_THAN_ZERO_STRICTLY, new ErrorField("bookingBalance", DomainErrorMessage.BOOKING_CHECK_AMOUNT_GREATER_THAN_ZERO_STRICTLY.getReasonPhrase()));
        this.paymentSourceDto = paymentSourceDto;
        this.isFromCredit = isFromCredit;
        this.amount = amount;
        this.bookingBalance = bookingBalance;
    }

    @Override
    public boolean isBroken() {
        if(paymentSourceDto.getExpense() && isFromCredit){
            return false;
        }
        return this.amount <= 0 || this.amount > this.bookingBalance;
    }

}
