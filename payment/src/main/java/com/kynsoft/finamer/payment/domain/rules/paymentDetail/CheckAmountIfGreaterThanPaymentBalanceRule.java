package com.kynsoft.finamer.payment.domain.rules.paymentDetail;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class CheckAmountIfGreaterThanPaymentBalanceRule extends BusinessRule {

    private final Double amount;
    private final Double paymentBalance;
    private final Double depositAmount;

    public CheckAmountIfGreaterThanPaymentBalanceRule(Double amount, Double paymentBalance, Double depositAmount) {
        super(DomainErrorMessage.CHECK_PAYMENT_DETAILS_AMOUNT_GREATER_THAN_ZERO, new ErrorField("amount", DomainErrorMessage.CHECK_PAYMENT_DETAILS_AMOUNT_GREATER_THAN_ZERO.getReasonPhrase()));
        this.amount = amount;
        this.paymentBalance = paymentBalance;
        this.depositAmount = depositAmount;
    }

    /**
     * Se puede seguir haciendo trx tipo check deposit si el valor que se desea enviar no supera el valor del
     * campo Payment Balance.
     * ****** DUDA EN ESTA REGLA. NO SE SI USAR EL: Deposit Amount o el Balance Amount.********
     * @return true or false
     */
    @Override
    public boolean isBroken() {
        return paymentBalance < amount;// || paymentBalance < (depositAmount + amount);
    }

}
