package com.kynsof.identity.application.command.geographiclocations.create;

import com.kynsof.identity.domain.dto.enumType.GeographicLocationType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateGeographicLocationRequest {
    private String name;
    private GeographicLocationType type;
    private UUID parent;
}
