package com.kynsof.identity.domain.dto;

import com.kynsof.identity.domain.dto.enumType.EBusinessStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class BusinessDto {
    private UUID id;
    private String name;
    private String latitude;
    private String longitude;
    private String description;
    private String logo;
    private String ruc;
    private String address;
    private EBusinessStatus status;
    private boolean deleted;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private LocalDateTime deleteAt;

    private GeographicLocationDto geographicLocationDto;
    private List<ModuleDto> moduleDtoList;

    public BusinessDto(UUID id, String name, String latitude, String longitude, String description, String logo,
                       String ruc, EBusinessStatus status, GeographicLocationDto geographicLocationDto, String address) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.logo = logo;
        this.ruc = ruc;
        this.status = status;
        this.geographicLocationDto = geographicLocationDto;
        this.address = address;
    }

    public BusinessDto(UUID id, String name, String latitude, String longitude, String description, String string) {
    }
}
