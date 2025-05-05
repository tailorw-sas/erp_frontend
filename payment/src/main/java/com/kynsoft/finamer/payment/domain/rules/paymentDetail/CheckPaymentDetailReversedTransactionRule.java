package com.kynsoft.finamer.payment.domain.rules.paymentDetail;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;

public class CheckPaymentDetailReversedTransactionRule extends BusinessRule {

    private final PaymentDetailDto paymentDetail;

    public CheckPaymentDetailReversedTransactionRule(PaymentDetailDto paymentDetail){
        super(DomainErrorMessage.CHECK_PAYMENT_DETAIL_IS_REVERSED, new ErrorField("id", DomainErrorMessage.CHECK_PAYMENT_DETAIL_IS_REVERSED.getReasonPhrase()));
        this.paymentDetail = paymentDetail;
    }

    @Override
    public boolean isBroken() {
        return paymentDetail.isReverseTransaction();
    }
}
