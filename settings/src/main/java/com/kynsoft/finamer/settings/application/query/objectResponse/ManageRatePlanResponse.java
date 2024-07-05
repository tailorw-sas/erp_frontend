package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageRatePlanDto;
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
public class ManageRatePlanResponse implements IResponse {

    private UUID id;
    private String code;
    private String name;
    private ManageHotelResponse hotel;
    private String description;
    private Status status;

    public ManageRatePlanResponse(ManageRatePlanDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.hotel = new ManageHotelResponse(dto.getHotel());
        this.description = dto.getDescription();
        this.status = dto.getStatus();
    }

}
