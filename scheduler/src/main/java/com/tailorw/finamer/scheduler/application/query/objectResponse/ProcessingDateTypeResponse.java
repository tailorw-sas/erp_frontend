package com.tailorw.finamer.scheduler.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
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
public class ProcessingDateTypeResponse implements IResponse {
    private UUID id;
    private String code;
    private Status status;

    public ProcessingDateTypeResponse(ProcessingDateTypeDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
    }
}
