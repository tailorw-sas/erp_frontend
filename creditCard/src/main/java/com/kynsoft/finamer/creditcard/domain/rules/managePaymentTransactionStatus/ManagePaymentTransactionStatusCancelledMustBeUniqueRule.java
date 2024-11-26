package com.kynsoft.finamer.creditcard.domain.rules.managePaymentTransactionStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.creditcard.domain.services.IManagePaymentTransactionStatus;

import java.util.UUID;

public class ManagePaymentTransactionStatusCancelledMustBeUniqueRule extends BusinessRule {

    private final IManagePaymentTransactionStatus service;

    private final UUID id;

    public ManagePaymentTransactionStatusCancelledMustBeUniqueRule(IManagePaymentTransactionStatus service,
                                                                   UUID id) {
        super(
                DomainErrorMessage.MANAGE_HOTEL_PAYMENT_STATUS_CHECK_CANCELLED,
                new ErrorField("cancelled", DomainErrorMessage.MANAGE_HOTEL_PAYMENT_STATUS_CHECK_CANCELLED.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByCancelledAndNotId(id) > 0;
    }

}
