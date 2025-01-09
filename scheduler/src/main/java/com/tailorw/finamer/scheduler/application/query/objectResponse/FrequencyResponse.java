package com.tailorw.finamer.scheduler.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.tailorw.finamer.scheduler.domain.dto.FrequencyDto;
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
public class FrequencyResponse implements IResponse {
    private UUID id;
    private String code;
    private Status status;

    public FrequencyResponse(FrequencyDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
    }
}
