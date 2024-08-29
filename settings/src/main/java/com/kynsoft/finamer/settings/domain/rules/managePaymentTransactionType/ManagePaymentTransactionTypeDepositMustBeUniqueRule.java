package com.kynsoft.finamer.settings.domain.rules.managePaymentTransactionType;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentTransactionTypeService;

import java.util.UUID;

public class ManagePaymentTransactionTypeDepositMustBeUniqueRule extends BusinessRule {

    private final IManagePaymentTransactionTypeService service;

    private final UUID id;

    public ManagePaymentTransactionTypeDepositMustBeUniqueRule(IManagePaymentTransactionTypeService service, UUID id) {
        super(
                DomainErrorMessage.MANAGE_PAYMENT_TRANSACTION_TYPE_CHECK_DEPOSIT_,
                new ErrorField("deposit", DomainErrorMessage.MANAGE_PAYMENT_TRANSACTION_TYPE_CHECK_DEPOSIT_.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByDepositAndNotId(id) > 0;
    }

}
