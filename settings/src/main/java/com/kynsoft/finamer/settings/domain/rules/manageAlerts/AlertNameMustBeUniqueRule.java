package com.kynsoft.finamer.settings.domain.rules.manageAlerts;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IAlertsService;

public class AlertNameMustBeUniqueRule extends BusinessRule {

    private final IAlertsService service;
    private final String code;

    public AlertNameMustBeUniqueRule(final IAlertsService service, final String code) {
        super(DomainErrorMessage.ITEM_ALREADY_EXITS, new ErrorField("name", DomainErrorMessage.ITEM_ALREADY_EXITS.toString()));
        this.service = service;
        this.code = code;
    }
    
    
    @Override
    public boolean isBroken() {
        return this.service.countByName(code) > 0;
    }
}
