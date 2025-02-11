package com.tailorw.finamer.scheduler.domain.rules.businessProcessScheduler;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.time.format.DateTimeFormatter;

public class BusinessProcessSchedulerExecutionDateFormatRule extends BusinessRule {

    private final String executionDate;
    private static final String DATE_REGEX = "^(\\d{4})-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$";

    public BusinessProcessSchedulerExecutionDateFormatRule(String executionDate){
        super(DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_EXECUTION_DATE_FORMAT_IS_NOT_VALID,
                new ErrorField("ExecutionDate", DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_EXECUTION_DATE_FORMAT_IS_NOT_VALID.getReasonPhrase()));
        this.executionDate = executionDate;
    }

    @Override
    public boolean isBroken() {
        if(executionDate == null || executionDate.isEmpty()){
            return false;
        }
        return !executionDate.matches(DATE_REGEX);
    }
}
