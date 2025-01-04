package com.kynsoft.finamer.creditcard.domain.rules.manageVCCTransactionType;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.creditcard.domain.services.IManageVCCTransactionTypeService;

import java.util.UUID;

public class ManageVCCTransactionTypeIsDefaultMustBeUniqueRule extends BusinessRule {

    private final IManageVCCTransactionTypeService service;

    private final UUID id;

    public ManageVCCTransactionTypeIsDefaultMustBeUniqueRule(IManageVCCTransactionTypeService service,
                                                             UUID id) {
        super(
                DomainErrorMessage.MANAGE_VCC_TRANSACTION_TYPE_CHECK_DEFAULT,
                new ErrorField("isDefault", DomainErrorMessage.MANAGE_VCC_TRANSACTION_TYPE_CHECK_DEFAULT.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByIsDefaultsAndNotSubcategoryAndNotId(id) > 0;
    }

}
