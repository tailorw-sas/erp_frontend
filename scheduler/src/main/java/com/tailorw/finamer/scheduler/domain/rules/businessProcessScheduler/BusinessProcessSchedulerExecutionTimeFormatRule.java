package com.tailorw.finamer.scheduler.domain.rules.businessProcessScheduler;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class BusinessProcessSchedulerExecutionTimeFormatRule extends BusinessRule {

    private final String executionTime;
    private static final String TIME_REGEX_24H = "^([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$";

    public BusinessProcessSchedulerExecutionTimeFormatRule(String executionTime){
        super(DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_EXECUTION_TIME_FORMAT_IS_NOT_VALID,
                new ErrorField("ExecutionTime", DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_EXECUTION_TIME_FORMAT_IS_NOT_VALID.getReasonPhrase()));
        this.executionTime = executionTime;
    }

    @Override
    public boolean isBroken() {
        if(executionTime == null || executionTime.isEmpty()){
            return false;
        }
        return !executionTime.matches(TIME_REGEX_24H);
    }
}
