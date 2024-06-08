package com.kynsoft.finamer.settings.domain.rules.managePaymentTransactionStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentTransactionStatusService;

import java.util.UUID;

public class ManagePaymentTransactionStatusCodeMustBeUniqueRule extends BusinessRule {

    private final IManagePaymentTransactionStatusService service;

    private final String code;

    private final UUID id;

    public ManagePaymentTransactionStatusCodeMustBeUniqueRule(IManagePaymentTransactionStatusService service,
            String code, UUID id) {
        super(
                DomainErrorMessage.MANAGE_PAYMENT_SOURCE_CODE_MUST_BY_UNIQUE,
                new ErrorField("code", "The code must be unique."));
        this.service = service;
        this.code = code;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByCodeAndNotId(code, id) > 0;
    }

}
