package com.kynsoft.finamer.invoicing.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageContactDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageContactResponse implements IResponse {

    private UUID id;
    private String code;
    private String description;
    private String name;
    private ManageHotelResponse manageHotel;
    private String email;
    private String phone;
    private Integer position;

    public ManageContactResponse(ManageContactDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.description = dto.getDescription();
        this.name = dto.getName();
        this.manageHotel = dto.getManageHotel() != null ? new ManageHotelResponse(dto.getManageHotel()) : null;
        this.email = dto.getEmail();
        this.phone = dto.getPhone();
        this.position = dto.getPosition();
    }
}
