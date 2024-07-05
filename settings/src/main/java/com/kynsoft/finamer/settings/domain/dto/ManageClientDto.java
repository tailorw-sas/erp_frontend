package com.kynsoft.finamer.settings.domain.dto;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageAgency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageClientDto {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Status status;
    private List<ManageAgencyDto> agencies;

    public ManageClientDto(UUID id, String code, String name, String description, Status status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.status = status;
    }
}
