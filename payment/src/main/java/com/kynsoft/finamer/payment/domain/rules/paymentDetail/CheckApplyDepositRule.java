package com.kynsoft.finamer.payment.domain.rules.paymentDetail;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class CheckApplyDepositRule extends BusinessRule {

    private final Boolean applyDeposit;

    public CheckApplyDepositRule(Boolean applyDeposit) {
        super(DomainErrorMessage.CHECK_APPLY_DEPOSIT, new ErrorField("applyDeposit", DomainErrorMessage.CHECK_APPLY_DEPOSIT.getReasonPhrase()));
        this.applyDeposit = applyDeposit;
    }

    /**
     * Verificando si la trx que se esta aplicando es de tipo apply_deposit.
     * @return 
     */
    @Override
    public boolean isBroken() {
        return !this.applyDeposit;
    }

}
