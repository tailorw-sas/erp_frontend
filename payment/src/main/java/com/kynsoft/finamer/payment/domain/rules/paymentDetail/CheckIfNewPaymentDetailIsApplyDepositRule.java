package com.kynsoft.finamer.payment.domain.rules.paymentDetail;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class CheckIfNewPaymentDetailIsApplyDepositRule extends BusinessRule {

    private final Boolean applyDeposit;

    public CheckIfNewPaymentDetailIsApplyDepositRule(Boolean applyDeposit) {
        super(DomainErrorMessage.CHECK_IF_NEW_PAYMENT_DETAIL_IS_APPLY_DEPOSIT, new ErrorField("transactionType", DomainErrorMessage.CHECK_IF_NEW_PAYMENT_DETAIL_IS_APPLY_DEPOSIT.getReasonPhrase()));
        this.applyDeposit = applyDeposit;
    }

    /**
     * Verificando si la trx que se esta aplicando al Payment, es de tipo Apply Details. No Aplica.
     * @return 
     */
    @Override
    public boolean isBroken() {
        return this.applyDeposit;
    }

}
