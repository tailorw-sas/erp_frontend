package com.kynsoft.finamer.payment.domain.rules.paymentDetail;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class CheckDepositToSplitRule extends BusinessRule {

    private final Boolean deposit;

    public CheckDepositToSplitRule(Boolean deposit) {
        super(DomainErrorMessage.CHECK_SPLIT_DEPOSIT, new ErrorField("deposit", DomainErrorMessage.CHECK_SPLIT_DEPOSIT.getReasonPhrase()));
        this.deposit = deposit;
    }

    /**
     * Verifica que solo las transacciones de tipo deposito pueden ser divididas.
     * @return 
     */
    @Override
    public boolean isBroken() {
        return !this.deposit;
    }

}
