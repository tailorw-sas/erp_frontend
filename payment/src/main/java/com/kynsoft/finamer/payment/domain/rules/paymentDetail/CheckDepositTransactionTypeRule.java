package com.kynsoft.finamer.payment.domain.rules.paymentDetail;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class CheckDepositTransactionTypeRule extends BusinessRule {

    private final Boolean deposit;

    public CheckDepositTransactionTypeRule(Boolean deposit) {
        super(DomainErrorMessage.CHECK_SPLIT_TRANSACTION_TYPE, new ErrorField("deposit", DomainErrorMessage.CHECK_SPLIT_TRANSACTION_TYPE.getReasonPhrase()));
        this.deposit = deposit;
    }

    /**
     * Verifica que la trx seleccionada para aplicar sea de tipo deposit.
     * @return 
     */
    @Override
    public boolean isBroken() {
        return !this.deposit;
    }

}
