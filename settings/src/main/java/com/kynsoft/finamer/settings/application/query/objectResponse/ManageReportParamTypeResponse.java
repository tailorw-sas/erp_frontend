package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageReportParamTypeDto;
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
public class ManageReportParamTypeResponse implements IResponse {

    private UUID id;
    private Status status;
    private String name;
    private String label;
    private Boolean hotel;
    private String source;

    public ManageReportParamTypeResponse(ManageReportParamTypeDto dto){
        this.id = dto.getId();
        this.status = dto.getStatus();
        this.name = dto.getName();
        this.label = dto.getLabel();
        this.hotel = dto.getHotel();
        this.source = dto.getSource();
    }
}
