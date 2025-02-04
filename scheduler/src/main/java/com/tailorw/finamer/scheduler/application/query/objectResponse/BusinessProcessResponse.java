package com.tailorw.finamer.scheduler.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessProcessResponse implements IResponse {

    private UUID id;
    private String code;
    private String processName;
    private String description;
    private String status;

    public BusinessProcessResponse(BusinessProcessDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.processName = dto.getProcessName();
        this.description = dto.getDescription();
        this.status = dto.getStatus().name();
    }
}
