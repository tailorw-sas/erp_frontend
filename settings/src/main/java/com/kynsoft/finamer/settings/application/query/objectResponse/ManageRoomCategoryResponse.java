package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageAgencyTypeDto;
import com.kynsoft.finamer.settings.domain.dto.ManageRoomCategoryDto;
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
public class ManageRoomCategoryResponse implements IResponse {

    private UUID id;
    private String code;
    private Status status;
    private String name;
    private String description;

    public ManageRoomCategoryResponse(ManageRoomCategoryDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
        this.name = dto.getName();
        this.description = dto.getDescription();
    }
}
