package com.kynsoft.finamer.creditcard.domain.rules.manageTransactionStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.creditcard.domain.services.IManageTransactionStatusService;

import java.util.UUID;

public class ManageTransactionCancelledStatusMustBeUniqueRule extends BusinessRule {

    private final IManageTransactionStatusService service;

    private final UUID id;

    public ManageTransactionCancelledStatusMustBeUniqueRule(IManageTransactionStatusService service, UUID id) {
        super(
                DomainErrorMessage.MANAGE_TRANSACTION_STATUS_CHECK_CANCELLED,
                new ErrorField("receivedStatus", DomainErrorMessage.MANAGE_TRANSACTION_STATUS_CHECK_CANCELLED.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByCancelledStatusAndNotId(id) > 0;
    }

}
