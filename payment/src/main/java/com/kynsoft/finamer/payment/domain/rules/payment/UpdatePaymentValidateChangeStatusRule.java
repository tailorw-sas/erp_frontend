package com.kynsoft.finamer.payment.domain.rules.payment;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentStatusDto;

public class UpdatePaymentValidateChangeStatusRule extends BusinessRule {

    private final ManagePaymentStatusDto allStatus;
    private final ManagePaymentStatusDto newStatus;
    private final boolean applyPayment;

    public UpdatePaymentValidateChangeStatusRule(ManagePaymentStatusDto allStatus, ManagePaymentStatusDto newStatus, boolean applyPayment) {
        super(DomainErrorMessage.UPDATE_PAYMENT_VALIDATE_CHANGE_STATUS, new ErrorField("paymentStatus", DomainErrorMessage.UPDATE_PAYMENT_VALIDATE_CHANGE_STATUS.getReasonPhrase()));
        this.allStatus = allStatus;
        this.newStatus = newStatus;
        this.applyPayment = applyPayment;
    }

    @Override
    public boolean isBroken() {
        return this.applyPayment && allStatus.isConfirmed() && newStatus.isCancelled();
    }

}
