package com.tailorw.finamer.scheduler.domain.dto;

import com.tailorw.finamer.scheduler.infrastructure.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessProcessSchedulerRuleDto {

    private UUID id;
    private FrequencyDto frequency;
    private IntervalTypeDto intervalType;
    private Boolean enableInterval;
    private ExecutionDateTypeDto executionDateType;
    private Boolean enableExecutionDateValue;
    private Boolean enableExecutionDate;
    private Boolean enableExecutionTime;
    private Status status;
}
