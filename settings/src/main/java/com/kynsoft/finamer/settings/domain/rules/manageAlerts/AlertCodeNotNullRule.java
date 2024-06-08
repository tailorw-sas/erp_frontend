package com.kynsoft.finamer.settings.domain.rules.manageAlerts;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class AlertCodeNotNullRule extends BusinessRule{
    
    private final String code;
    
    public AlertCodeNotNullRule(final String code) {
        super(DomainErrorMessage.ALERT_CODE_CANNOT_BE_EMPTY, new ErrorField("code", "Alert code cannot be empty"));
        this.code = code;
    }
    
    @Override
    public boolean isBroken() {
        return this.code == null || this.code.isEmpty();
    }
}
