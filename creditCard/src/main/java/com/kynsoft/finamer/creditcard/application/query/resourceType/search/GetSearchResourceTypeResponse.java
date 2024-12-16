package com.kynsoft.finamer.creditcard.application.query.resourceType.search;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ResourceTypeDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetSearchResourceTypeResponse implements IResponse {
    private UUID id;
    private String code;
    private String name;
    private Status status;

    public GetSearchResourceTypeResponse(ResourceTypeDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
    }    
}
