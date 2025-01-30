package com.tailorw.finamer.scheduler.domain.rules.businessProcess;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessDto;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.Status;

public class BusinessProcessInactiveRule extends BusinessRule {

    private final BusinessProcessDto businessProcessDto;

    public BusinessProcessInactiveRule(BusinessProcessDto businessProcessDto){
        super(DomainErrorMessage.BUSINESS_PROCESS_IS_INACTIVE, new ErrorField("BusinessProcessID", DomainErrorMessage.BUSINESS_PROCESS_IS_INACTIVE.getReasonPhrase()));
        this.businessProcessDto = businessProcessDto;
    }

    @Override
    public boolean isBroken() {
        return businessProcessDto.getStatus().equals(Status.INACTIVE) || businessProcessDto.getStatus().equals(Status.DELETED);
    }
}
