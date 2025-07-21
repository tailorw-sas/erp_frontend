package com.tailorw.finamer.scheduler.domain.rules.businessProcessScheduler;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class BusinessProcessSchedulerTimeFormatRule extends BusinessRule {

    private final String time;
    private static final String TIME_FORMAT_REGEX = "^([01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d$";

    public BusinessProcessSchedulerTimeFormatRule(String time){
        super(DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_START_END_TIME_FORMAT_IS_NOT_VALID,
                new ErrorField("StartTime or EndTime", DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_START_END_TIME_FORMAT_IS_NOT_VALID.getReasonPhrase()));
        this.time = time;
    }

    @Override
    public boolean isBroken() {
        if(time == null){
            return false;
        }
        return !time.matches(TIME_FORMAT_REGEX);
    }
}
