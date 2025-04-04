package com.kynsoft.finamer.settings.application.query.objectResponse.manageHotelGroup;

import com.kynsoft.finamer.settings.application.query.objectResponse.ManageHotelResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.infrastructure.projections.ManageHotelProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageHotelBasicResponse {

    private UUID id;
    private String code;
    private Status status;
    private String name;

    public ManageHotelBasicResponse(ManageHotelDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
        this.name = dto.getName();
    }

    public ManageHotelBasicResponse(ManageHotelProjection projection){
        this.id = projection.getId();
        this.code = projection.getCode();
        this.status = projection.getStatus();
        this.name = projection.getName();
    }
}
