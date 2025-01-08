package com.kynsoft.finamer.insis.application.query.objectResponse.manageB2BPartnerType;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.insis.domain.dto.ManageB2BPartnerTypeDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ManageB2BPartnerTypeResponse implements IResponse {
    private UUID id;
    private String code;
    private String name;
    private String status;

    public ManageB2BPartnerTypeResponse(ManageB2BPartnerTypeDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
    }
}
