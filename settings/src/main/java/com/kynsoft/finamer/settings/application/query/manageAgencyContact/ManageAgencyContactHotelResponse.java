package com.kynsoft.finamer.settings.application.query.manageAgencyContact;

import com.kynsoft.finamer.settings.domain.dto.ManageHotelDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageAgencyContactHotelResponse {

    private UUID id;
    private String code;
    private String name;

    public ManageAgencyContactHotelResponse(ManageHotelDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
    }
}
