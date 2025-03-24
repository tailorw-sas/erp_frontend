package com.kynsoft.finamer.insis.application.query.objectResponse.manageRoomCategory;

import com.kynsoft.finamer.insis.domain.dto.ManageRoomCategoryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManageRoomCategoryResponse {
    private UUID id;
    private String code;
    private String name;
    private String status;

    public ManageRoomCategoryResponse(ManageRoomCategoryDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
    }
}
