package com.kynsoft.finamer.creditcard.domain.rules.manageTransactionStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.creditcard.domain.services.IManageTransactionStatusService;

import java.util.UUID;

public class ManageTransactionReconciledStatusMustBeUniqueRule extends BusinessRule {

    private final IManageTransactionStatusService service;

    private final UUID id;

    public ManageTransactionReconciledStatusMustBeUniqueRule(IManageTransactionStatusService service, UUID id) {
        super(
                DomainErrorMessage.MANAGE_TRANSACTION_STATUS_CHECK_RECONCILED,
                new ErrorField("reconciledStatus", DomainErrorMessage.MANAGE_TRANSACTION_STATUS_CHECK_RECONCILED.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByReconciledStatusAndNotId(id) > 0;
    }

}
