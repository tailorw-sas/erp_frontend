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
public class BusinessProcessSchedulerProcessingRuleDto {

    private UUID id;
    private FrequencyDto frequency;
    private ProcessingDateTypeDto processingDateType;
    private Boolean enableProcessingDateValue;
    private Boolean enableProcessingDate;
    private Status status;
}
