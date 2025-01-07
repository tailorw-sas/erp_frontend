package com.kynsoft.finamer.insis.application.query.objectResponse.innsistHotelRoomType;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.insis.domain.dto.InnsistHotelRoomTypeDto;
import com.kynsoft.finamer.insis.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.insis.domain.dto.ManageTradingCompanyDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class InnsistHotelRoomTypeResponse implements IResponse {
    public UUID id;
    public ManageHotelDto hotel;
    private String roomTypePrefix;
    private String status;
    private LocalDateTime updatedAt;

    public InnsistHotelRoomTypeResponse(InnsistHotelRoomTypeDto dto){
        this.id = dto.getId();
        this.hotel = dto.getHotel();
        this.roomTypePrefix = dto.getRoomTypePrefix();
        this.status = dto.getStatus();
        this.updatedAt = dto.getUpdatedAt();
    }
}
