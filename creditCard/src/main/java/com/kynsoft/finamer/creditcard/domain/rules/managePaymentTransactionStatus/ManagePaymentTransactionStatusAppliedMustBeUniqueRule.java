package com.kynsoft.finamer.creditcard.domain.rules.managePaymentTransactionStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.creditcard.domain.services.IManagePaymentTransactionStatusService;

import java.util.UUID;

public class ManagePaymentTransactionStatusAppliedMustBeUniqueRule extends BusinessRule {

    private final IManagePaymentTransactionStatusService service;

    private final UUID id;

    public ManagePaymentTransactionStatusAppliedMustBeUniqueRule(IManagePaymentTransactionStatusService service,
                                                                 UUID id) {
        super(
                DomainErrorMessage.MANAGE_HOTEL_PAYMENT_STATUS_CHECK_APPLIED,
                new ErrorField("cancelled", DomainErrorMessage.MANAGE_HOTEL_PAYMENT_STATUS_CHECK_APPLIED.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByAppliedAndNotId(id) > 0;
    }

}
