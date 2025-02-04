package com.tailorw.finamer.scheduler.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessSchedulerExecutionRuleDto;
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
public class BusinessProcessSchedulerExecutionRuleResponse implements IResponse {

    private UUID id;
    private FrequencyResponse frequency;
    private IntervalTypeResponse intervalType;
    private Boolean enableInterval;
    private Boolean enableExecutionDateType;
    private ExecutionDateTypeResponse executionDateType;
    private Boolean enableExecutionDateValue;
    private Boolean enableExecutionDate;
    private Boolean enableExecutionTime;
    private Status status;

    public BusinessProcessSchedulerExecutionRuleResponse(BusinessProcessSchedulerExecutionRuleDto dto){
        this.id = dto.getId();
        this.frequency = Objects.nonNull(dto.getFrequency()) ? new FrequencyResponse(dto.getFrequency()) : null;
        this.intervalType = Objects.nonNull(dto.getIntervalType()) ? new IntervalTypeResponse(dto.getIntervalType()) : null;
        this.enableInterval = dto.getEnableInterval();
        this.enableExecutionDateType = dto.getEnableExecutionDateType();
        this.executionDateType = Objects.nonNull(dto.getExecutionDateType()) ? new ExecutionDateTypeResponse(dto.getExecutionDateType()) : null;
        this.enableExecutionDateValue = dto.getEnableExecutionDateValue();
        this.enableExecutionDate = dto.getEnableExecutionDate();
        this.enableExecutionTime = dto.getEnableExecutionTime();
        this.status = dto.getStatus();
    }
}
