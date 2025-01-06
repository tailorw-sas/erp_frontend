package com.kynsoft.finamer.insis.application.command.innsistHotelRoomType.create;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateInnsistHotelRoomTypeRequest {
    public UUID hotel;
    private String roomTypePrefix;
    private String status;
}
