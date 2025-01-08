package com.tailorw.finamer.scheduler.application.command.businessProcessScheduler.update;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateBusinessProcessSchedulerRequest {

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
    private String status;
    private UUID process;
    private boolean allowsQueueing;
}
