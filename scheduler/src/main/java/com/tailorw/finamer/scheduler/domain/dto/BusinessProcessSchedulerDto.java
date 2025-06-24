package com.tailorw.finamer.scheduler.domain.dto;

import com.tailorw.finamer.scheduler.infrastructure.model.enums.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BusinessProcessSchedulerDto {
    private UUID id;
    private FrequencyDto frequency;
    private IntervalTypeDto intervalType;
    private Integer interval;
    private ExecutionDateTypeDto executionDateType;
    private String executionDateValue;
    private LocalDate executionDate;
    private LocalTime executionTime;
    private ProcessingDateTypeDto processingDateType;
    private Integer processingDateValue;
    private LocalDate processingDate;
    private Status status;
    private String params;
    private LocalDateTime lastExecutionDatetime;
    private boolean isInProcess;
    private BusinessProcessDto process;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private boolean allowsQueueing;
    public LocalTime startTime;
    public LocalTime endTime;
}
