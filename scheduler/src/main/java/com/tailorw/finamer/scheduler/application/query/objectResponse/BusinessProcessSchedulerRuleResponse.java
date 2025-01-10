package com.tailorw.finamer.scheduler.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessSchedulerRuleDto;
import com.tailorw.finamer.scheduler.domain.dto.ExecutionDateTypeDto;
import com.tailorw.finamer.scheduler.domain.dto.FrequencyDto;
import com.tailorw.finamer.scheduler.domain.dto.IntervalTypeDto;
import com.tailorw.finamer.scheduler.infrastructure.model.ExecutionDateType;
import com.tailorw.finamer.scheduler.infrastructure.model.Frequency;
import com.tailorw.finamer.scheduler.infrastructure.model.IntervalType;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessProcessSchedulerRuleResponse implements IResponse {

    private UUID id;
    private FrequencyResponse frequency;
    private IntervalTypeResponse intervalType;
    private Boolean enableInterval;
    private ExecutionDateTypeResponse executionDateType;
    private Boolean enableExecutionDateValue;
    private Boolean enableExecutionDate;
    private Boolean enableExecutionTime;
    private Status status;

    public BusinessProcessSchedulerRuleResponse(BusinessProcessSchedulerRuleDto dto){
        this.id = dto.getId();
        this.frequency = Objects.nonNull(dto.getFrequency()) ? new FrequencyResponse(dto.getFrequency()) : null;
        this.intervalType = Objects.nonNull(dto.getIntervalType()) ? new IntervalTypeResponse(dto.getIntervalType()) : null;
        this.enableInterval = dto.getEnableInterval();
        this.executionDateType = Objects.nonNull(dto.getExecutionDateType()) ? new ExecutionDateTypeResponse(dto.getExecutionDateType()) : null;
        this.enableExecutionDateValue = dto.getEnableExecutionDateValue();
        this.enableExecutionDate = dto.getEnableExecutionDate();
        this.enableExecutionTime = dto.getEnableExecutionTime();
        this.status = dto.getStatus();
    }
}
