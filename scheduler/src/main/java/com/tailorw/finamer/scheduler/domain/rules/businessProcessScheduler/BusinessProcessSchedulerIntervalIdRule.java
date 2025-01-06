package com.tailorw.finamer.scheduler.domain.rules.businessProcessScheduler;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.tailorw.finamer.scheduler.domain.services.IIntervalTypeService;

import java.util.Objects;
import java.util.UUID;

public class BusinessProcessSchedulerIntervalIdRule extends BusinessRule {

    private final UUID id;
    private final IIntervalTypeService intervalTypeService;

    public BusinessProcessSchedulerIntervalIdRule(UUID id, IIntervalTypeService intervalTypeService){
        super(DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_INTERVAL_TYPE_NOT_FOUND, new ErrorField("intervalType", DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_INTERVAL_TYPE_NOT_FOUND.getReasonPhrase()));
        this.id = id;
        this.intervalTypeService = intervalTypeService;
    }

    @Override
    public boolean isBroken() {
        return Objects.isNull(intervalTypeService.getById(id));
    }
}
