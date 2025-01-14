package com.kynsoft.finamer.insis.application.query.innsistHotelRoomType.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindInnsistHotelRoomTypeByIdQuery implements IQuery {
    private final UUID id;

    public FindInnsistHotelRoomTypeByIdQuery(UUID id){
        this.id = id;
    }
}
