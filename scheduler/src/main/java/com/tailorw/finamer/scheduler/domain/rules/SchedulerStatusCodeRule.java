package com.tailorw.finamer.scheduler.domain.rules;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.Status;

public class SchedulerStatusCodeRule extends BusinessRule {

    private final String code;

    public SchedulerStatusCodeRule(String code){
        super(DomainErrorMessage.SCHEDULER_STATUS_CODE_NOT_FOUND, new ErrorField("status", DomainErrorMessage.SCHEDULER_STATUS_CODE_NOT_FOUND.getReasonPhrase()));
        this.code = code;
    }

    @Override
    public boolean isBroken() {
        return !Status.exists(code);
    }
}
