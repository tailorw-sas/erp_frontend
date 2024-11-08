package com.kynsoft.finamer.creditcard.domain.rules.manageReconcileTransactionStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.creditcard.domain.services.IManageReconcileTransactionStatusService;

import java.util.UUID;

public class ManageReconcileTransactionStatusCompletedMustBeUniqueRule extends BusinessRule {

    private final IManageReconcileTransactionStatusService service;

    private final UUID id;

    public ManageReconcileTransactionStatusCompletedMustBeUniqueRule(IManageReconcileTransactionStatusService service,
                                                                     UUID id) {
        super(
                DomainErrorMessage.MANAGE_RECONCILE_TRANSACTION_STATUS_CHECK_COMPLETED,
                new ErrorField("completed", DomainErrorMessage.MANAGE_RECONCILE_TRANSACTION_STATUS_CHECK_COMPLETED.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByCompletedAndNotId(id) > 0;
    }

}
