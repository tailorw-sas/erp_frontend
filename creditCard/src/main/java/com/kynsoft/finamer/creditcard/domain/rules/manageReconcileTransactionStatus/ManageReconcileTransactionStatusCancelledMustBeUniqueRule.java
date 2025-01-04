package com.kynsoft.finamer.creditcard.domain.rules.manageReconcileTransactionStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.creditcard.domain.services.IManageReconcileTransactionStatusService;

import java.util.UUID;

public class ManageReconcileTransactionStatusCancelledMustBeUniqueRule extends BusinessRule {

    private final IManageReconcileTransactionStatusService service;

    private final UUID id;

    public ManageReconcileTransactionStatusCancelledMustBeUniqueRule(IManageReconcileTransactionStatusService service,
                                                                     UUID id) {
        super(
                DomainErrorMessage.MANAGE_RECONCILE_TRANSACTION_STATUS_CHECK_CANCELLED,
                new ErrorField("cancelled", DomainErrorMessage.MANAGE_RECONCILE_TRANSACTION_STATUS_CHECK_CANCELLED.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByCancelledAndNotId(id) > 0;
    }

}
