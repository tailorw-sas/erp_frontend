package com.kynsoft.finamer.invoicing.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;

import com.kynsoft.finamer.invoicing.domain.dto.ManageCityStateDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageCityStateResponse implements IResponse {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Status status;
    private ManagerCountryResponse country;
    private ManagerTimeZoneResponse timeZone;

    public ManageCityStateResponse(ManageCityStateDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.country = dto.getCountry() != null ? new ManagerCountryResponse(dto.getCountry()) : null;
        this.timeZone = dto.getTimeZone() != null ? new ManagerTimeZoneResponse(dto.getTimeZone()) : null;
    }

}
