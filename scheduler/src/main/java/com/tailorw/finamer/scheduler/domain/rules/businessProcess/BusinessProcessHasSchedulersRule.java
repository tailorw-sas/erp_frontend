package com.tailorw.finamer.scheduler.domain.rules.businessProcess;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessService;

import java.util.UUID;

public class BusinessProcessHasSchedulersRule extends BusinessRule {

    private final UUID id;
    private final IBusinessProcessService service;

    public BusinessProcessHasSchedulersRule(UUID id, IBusinessProcessService service){
        super(DomainErrorMessage.BUSINESS_PROCESS_HAS_SCHEDULERS, new ErrorField("BusinessProcessId", DomainErrorMessage.BUSINESS_PROCESS_HAS_SCHEDULERS.getReasonPhrase()));
        this.id = id;
        this.service = service;
    }

    @Override
    public boolean isBroken() {
        return service.countActiveAndInactiveBusinessProcessSchedulers(id) > 0;
    }
}
