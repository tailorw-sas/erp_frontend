package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageAlertsDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageAlertsResponse implements IResponse {
    private UUID id;
    private String code;
    private String name;
    private Boolean popup;
    private Status status;
    private String description;
    
    public ManageAlertsResponse(ManageAlertsDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.popup = dto.getPopup();
        this.status = dto.getStatus();
        this.description = dto.getDescription();
    }
}
