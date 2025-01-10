package com.tailorw.finamer.scheduler.domain.rules.businessProcessScheduler;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.tailorw.finamer.scheduler.domain.services.IFrecuencyService;

import java.util.Objects;
import java.util.UUID;

public class BusinessProcessSchedulerFrecuencyIdRule extends BusinessRule {

    private final UUID id;
    private final IFrecuencyService frecuencyService;

    public BusinessProcessSchedulerFrecuencyIdRule(UUID id, IFrecuencyService frecuencyService) {
        super(DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_FRECUENCY_TYPE_NOT_FOUND, new ErrorField("frecuencyType", DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_FRECUENCY_TYPE_NOT_FOUND.getReasonPhrase()));
        this.id = id;
        this.frecuencyService = frecuencyService;
    }

    @Override
    public boolean isBroken() {
        return Objects.isNull(frecuencyService.getById(id));
    }
}
