package com.kynsoft.finamer.payment.domain.rules.paymentDetail;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;

import java.util.List;

public class CheckPaymentDetailDepositTypeIsApplyRule extends BusinessRule {

    private final List<PaymentDetailDto> children;

    public CheckPaymentDetailDepositTypeIsApplyRule(List<PaymentDetailDto> children) {
        super(DomainErrorMessage.CHECK_APPLY_DEPOSIT_TO_APPLIED_PAYMENT, new ErrorField("amount", DomainErrorMessage.CHECK_APPLY_DEPOSIT_TO_APPLIED_PAYMENT.getReasonPhrase()));
        this.children = children;
    }

    /**
     * Debe ser mayor estricto que 0.
     * @return true or false
     */
    @Override
    public boolean isBroken() {
        return this.children != null && !this.children.isEmpty();
    }

}
