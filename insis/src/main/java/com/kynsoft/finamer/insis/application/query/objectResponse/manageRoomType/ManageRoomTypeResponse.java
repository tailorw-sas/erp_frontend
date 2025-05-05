package com.kynsoft.finamer.insis.application.query.objectResponse.manageRoomType;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.insis.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.insis.domain.dto.ManageRoomTypeDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ManageRoomTypeResponse implements IResponse {
    private UUID id;
    private String code;
    private String name;
    private String status;
    private boolean deleted;
    private LocalDateTime updatedAt;
    private ManageHotelDto hotel;

    public ManageRoomTypeResponse(ManageRoomTypeDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
        this.deleted = dto.isDeleted();
        this.updatedAt = dto.getUpdatedAt();
        this.hotel = dto.getHotel();
    }
}
