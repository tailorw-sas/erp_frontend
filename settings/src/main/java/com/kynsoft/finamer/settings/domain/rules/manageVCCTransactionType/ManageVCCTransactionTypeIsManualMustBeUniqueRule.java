package com.kynsoft.finamer.settings.domain.rules.manageVCCTransactionType;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageVCCTransactionTypeService;

import java.util.UUID;

public class ManageVCCTransactionTypeIsManualMustBeUniqueRule extends BusinessRule {

    private final IManageVCCTransactionTypeService service;

    private final UUID id;

    public ManageVCCTransactionTypeIsManualMustBeUniqueRule(IManageVCCTransactionTypeService service,
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
        return this.service.countByManualAndNotId(id) > 0;
    }

}
