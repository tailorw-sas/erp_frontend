package com.kynsoft.finamer.insis.application.command.innsistHotelRoomType.update;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateInnsistHotelRoomTypeRequest {
    private UUID id;
    public UUID hotel;
    private String roomTypePrefix;
    private String status;
}
