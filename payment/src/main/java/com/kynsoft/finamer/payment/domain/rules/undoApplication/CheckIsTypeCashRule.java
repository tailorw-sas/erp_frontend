package com.kynsoft.finamer.payment.domain.rules.undoApplication;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;

public class CheckIsTypeCashRule extends BusinessRule {

    private final PaymentDetailDto paymentDetailDto;

    public CheckIsTypeCashRule(PaymentDetailDto paymentDetailDto) {
        super(
                DomainErrorMessage.UNDO_APPLICATION_TYPE_CASH_OR_APPLY_DEPOSIT,
                new ErrorField("applyPayment", DomainErrorMessage.UNDO_APPLICATION_TYPE_CASH_OR_APPLY_DEPOSIT.getReasonPhrase())
        );
        this.paymentDetailDto = paymentDetailDto;
    }

    @Override
    public boolean isBroken() {
        return !this.paymentDetailDto.getTransactionType().getApplyDeposit() &&  !this.paymentDetailDto.getTransactionType().getCash();
    }

}
