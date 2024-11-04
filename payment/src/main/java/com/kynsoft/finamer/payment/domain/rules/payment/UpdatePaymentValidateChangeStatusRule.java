package com.kynsoft.finamer.payment.domain.rules.payment;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentStatusDto;

public class UpdatePaymentValidateChangeStatusRule extends BusinessRule {

    private final ManagePaymentStatusDto oldStatus;
    private final ManagePaymentStatusDto newStatus;
    private final boolean applyPayment;

    public UpdatePaymentValidateChangeStatusRule(ManagePaymentStatusDto oldStatus, ManagePaymentStatusDto newStatus, boolean applyPayment) {
        super(DomainErrorMessage.UPDATE_PAYMENT_VALIDATE_CHANGE_STATUS, new ErrorField("paymentStatus", DomainErrorMessage.UPDATE_PAYMENT_VALIDATE_CHANGE_STATUS.getReasonPhrase()));
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.applyPayment = applyPayment;
    }

    @Override
    public boolean isBroken() {
        return this.applyPayment && oldStatus.isConfirmed() && newStatus.isCancelled();
    }

}
