package com.kynsoft.finamer.payment.domain.rules.paymentDetail;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;

public class CheckDeletePaymentDetailsDepositRule extends BusinessRule {

    private final PaymentDetailDto detailDto;

    public CheckDeletePaymentDetailsDepositRule(PaymentDetailDto detailDto) {
        super(DomainErrorMessage.DELETE_PAYMENT_DETAILS, new ErrorField("id", DomainErrorMessage.DELETE_PAYMENT_DETAILS.getReasonPhrase()));
        this.detailDto = detailDto;
    }

    @Override
    public boolean isBroken() {
        return !this.detailDto.getChildren().isEmpty();
    }

}
