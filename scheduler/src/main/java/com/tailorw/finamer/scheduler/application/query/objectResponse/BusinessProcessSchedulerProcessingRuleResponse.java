package com.tailorw.finamer.scheduler.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessSchedulerProcessingRuleDto;
import com.tailorw.finamer.scheduler.domain.dto.ProcessingDateTypeDto;
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
public class BusinessProcessSchedulerProcessingRuleResponse implements IResponse {
    private UUID id;
    private ProcessingDateTypeDto processingDateType;
    private Boolean enableProcessingDateValue;
    private Boolean enableProcessingDate;
    private Status status;

    public BusinessProcessSchedulerProcessingRuleResponse(BusinessProcessSchedulerProcessingRuleDto dto){
        this.id = dto.getId();
        this.processingDateType = dto.getProcessingDateType();
        this.enableProcessingDateValue = dto.getEnableProcessingDateValue();
        this.enableProcessingDate = dto.getEnableProcessingDate();
        this.status = dto.getStatus();
    }
}
