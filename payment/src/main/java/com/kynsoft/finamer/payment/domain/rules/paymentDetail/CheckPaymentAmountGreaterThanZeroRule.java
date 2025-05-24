package com.kynsoft.finamer.payment.domain.rules.paymentDetail;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentSourceDto;

public class CheckPaymentAmountGreaterThanZeroRule extends BusinessRule {

    private final ManagePaymentSourceDto paymentSourceDto;
    private final Double amount;

    public CheckPaymentAmountGreaterThanZeroRule(ManagePaymentSourceDto paymentSourceDto, Double amount) {
        super(DomainErrorMessage.CHECK_PAYMENT_AMOUNT_GREATER_THAN_ZERO, new ErrorField("amount", DomainErrorMessage.CHECK_PAYMENT_AMOUNT_GREATER_THAN_ZERO.getReasonPhrase()));
        this.paymentSourceDto = paymentSourceDto;
        this.amount = amount;
    }

    /**
     * Payment Amount: Debe ser mayor estricto que 0 y es el monto del Pago
     * @return true or false
     */
    @Override
    public boolean isBroken() {
        if(paymentSourceDto.getIsBank()){
            return this.amount <= 0;
        }
        return false;
    }

}
