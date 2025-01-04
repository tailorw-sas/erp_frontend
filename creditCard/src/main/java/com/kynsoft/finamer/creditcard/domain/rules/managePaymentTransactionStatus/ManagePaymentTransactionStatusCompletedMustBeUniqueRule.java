package com.kynsoft.finamer.creditcard.domain.rules.managePaymentTransactionStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.creditcard.domain.services.IManagePaymentTransactionStatusService;

import java.util.UUID;

public class ManagePaymentTransactionStatusCompletedMustBeUniqueRule extends BusinessRule {

    private final IManagePaymentTransactionStatusService service;

    private final UUID id;

    public ManagePaymentTransactionStatusCompletedMustBeUniqueRule(IManagePaymentTransactionStatusService service,
                                                                   UUID id) {
        super(
                DomainErrorMessage.MANAGE_HOTEL_PAYMENT_STATUS_CHECK_COMPLETED,
                new ErrorField("completed", DomainErrorMessage.MANAGE_HOTEL_PAYMENT_STATUS_CHECK_COMPLETED.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByCompletedAndNotId(id) > 0;
    }

}
