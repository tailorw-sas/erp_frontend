package com.tailorw.finamer.scheduler.application.command.businessProcessScheduler.create;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateBusinessProcessSchedulerRequest {
    private UUID frequency;
    private UUID intervalType;
    private Integer interval;
    private UUID executionDateType;
    private String executionDateValue;
    private String executionDate;
    private String executionTime;
    private UUID processingDateType;
    private Integer processingDateValue;
    private String processingDate;
    private String params;
    private UUID process;
    private boolean allowsQueueing;
    private String startTime;
    private String endTime;
}
