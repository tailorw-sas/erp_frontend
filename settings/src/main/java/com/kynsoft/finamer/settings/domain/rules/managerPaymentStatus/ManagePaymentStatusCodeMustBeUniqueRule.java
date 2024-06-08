package com.kynsoft.finamer.settings.domain.rules.managerPaymentStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManagerPaymentStatusService;

import java.util.UUID;

public class ManagePaymentStatusCodeMustBeUniqueRule extends BusinessRule {
    
    private final IManagerPaymentStatusService service;
    private final String code;
    private final UUID id;
    
    public ManagePaymentStatusCodeMustBeUniqueRule(final IManagerPaymentStatusService service, final String code, UUID id) {
        super(
                DomainErrorMessage.ITEM_ALREADY_EXITS,
                new ErrorField("code", DomainErrorMessage.ITEM_ALREADY_EXITS.toString())
        );
        this.service = service;
        this.code = code;
        this.id = id;
    }
    @Override
    public boolean isBroken() {
        return service.countByCode(code, id) > 0;
    }
}
