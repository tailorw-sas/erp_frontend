package com.kynsof.identity.application.query.business.geographiclocation.getall;

import com.kynsof.identity.domain.dto.GeographicLocationDto;
import com.kynsof.identity.domain.dto.enumType.GeographicLocationType;
import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class GeographicLocationResponse implements IResponse, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String name;
    private GeographicLocationType type;
    private GeographicLocationDto parent;

    public GeographicLocationResponse(GeographicLocationDto contactInfoDto) {
        this.id = contactInfoDto.getId();
        this.name = contactInfoDto.getName();
        this.type = contactInfoDto.getType();
        this.parent = contactInfoDto.getParent();
    }
}
