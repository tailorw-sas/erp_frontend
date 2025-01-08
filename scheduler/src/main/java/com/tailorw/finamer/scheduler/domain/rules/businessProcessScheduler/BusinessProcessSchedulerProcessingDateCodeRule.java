package com.tailorw.finamer.scheduler.domain.rules.businessProcessScheduler;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.tailorw.finamer.scheduler.domain.services.IProcessingDateTypeService;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.EProcessingDateType;

import java.util.Objects;
import java.util.UUID;

public class BusinessProcessSchedulerProcessingDateCodeRule extends BusinessRule {

    private final UUID id;
    private final IProcessingDateTypeService processingDateTypeService;

    public BusinessProcessSchedulerProcessingDateCodeRule(UUID id,
                                                          IProcessingDateTypeService processingDateTypeService){
        super(DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_PROCESSING_DATE_TYPE_NOT_FOUND,
                new ErrorField("ProcessingDateType", DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_PROCESSING_DATE_TYPE_NOT_FOUND.getReasonPhrase()));
        this.id = id;
        this.processingDateTypeService = processingDateTypeService;
    }

    @Override
    public boolean isBroken() {
        return Objects.isNull(processingDateTypeService.getById(id));
    }
}
