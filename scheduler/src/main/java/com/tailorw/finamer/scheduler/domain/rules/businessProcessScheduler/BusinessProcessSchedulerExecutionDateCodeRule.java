package com.tailorw.finamer.scheduler.domain.rules.businessProcessScheduler;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.tailorw.finamer.scheduler.domain.dto.ExecutionDateTypeDto;
import com.tailorw.finamer.scheduler.domain.services.IExecutionDateTypeService;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.EExecutionDateType;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.EFrequency;

import java.util.UUID;

public class BusinessProcessSchedulerExecutionDateCodeRule extends BusinessRule {

    private final String frequencyCode;
    private final UUID id;
    private final IExecutionDateTypeService executionDateTypeService;

    public BusinessProcessSchedulerExecutionDateCodeRule(String frequencyCode,
                                                         UUID id,
                                                         IExecutionDateTypeService executionDateTypeService){
        super(DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_EXECUTION_DATE_TYPE_NOT_FOUND,
                new ErrorField("ExecutionDateType", DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_EXECUTION_DATE_TYPE_NOT_FOUND.getReasonPhrase()));
        this.frequencyCode = frequencyCode;
        this.id = id;
        this.executionDateTypeService = executionDateTypeService;
    }
    @Override
    public boolean isBroken() {
        if(EFrequency.getCode(frequencyCode).equals(EFrequency.DAILY)){
            return false;
        }
        try{
            ExecutionDateTypeDto dto = executionDateTypeService.getById(id);
            return false;
        }catch (Exception ex){
            return true;
        }
    }
}
