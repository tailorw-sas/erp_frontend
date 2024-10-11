package com.kynsoft.finamer.settings.domain.dto;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageClientDto implements Serializable {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Status status;
   // private List<ManageAgencyDto> agencies;
    private Boolean isNightType;

//    public ManageClientDto(UUID id, String code, String name, String description, Status status, Boolean isNightType) {
//        this.id = id;
//        this.code = code;
//        this.name = name;
//        this.description = description;
//        this.status = status;
//        this.isNightType = isNightType;
//    }
}
