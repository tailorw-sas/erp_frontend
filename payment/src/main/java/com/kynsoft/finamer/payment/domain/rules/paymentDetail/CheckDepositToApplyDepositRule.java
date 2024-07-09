package com.kynsoft.finamer.payment.domain.rules.paymentDetail;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class CheckDepositToApplyDepositRule extends BusinessRule {

    private final Boolean deposit;

    public CheckDepositToApplyDepositRule(Boolean deposit) {
        super(DomainErrorMessage.CHECK_DEPOSIT_TO_APPLY, new ErrorField("deposit", DomainErrorMessage.CHECK_DEPOSIT_TO_APPLY.getReasonPhrase()));
        this.deposit = deposit;
    }

    /**
     * Verifica que la trx seleccionada para aplicar apply_deposit sea de tipo deposit.
     * @return 
     */
    @Override
    public boolean isBroken() {
        return !this.deposit;
    }

}
