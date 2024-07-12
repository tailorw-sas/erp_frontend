package com.kynsoft.finamer.creditcard.domain.rules.refundTransaction;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;

public class RefundTransactionCompareParentAmountRule extends BusinessRule {

    private final ITransactionService service;

    private final Long parentId;

    private final Double amount;

    public RefundTransactionCompareParentAmountRule(ITransactionService service, Long parentId, Double amount) {
        super(
                DomainErrorMessage.VCC_REFUND_NOT_ACCEPTED,
                new ErrorField("amount", DomainErrorMessage.VCC_REFUND_NOT_ACCEPTED.getReasonPhrase())
        );
        this.service = service;
        this.parentId = parentId;
        this.amount = amount;
    }

    @Override
    public boolean isBroken() {
        return this.service.compareParentTransactionAmount(parentId, amount);
    }
}
