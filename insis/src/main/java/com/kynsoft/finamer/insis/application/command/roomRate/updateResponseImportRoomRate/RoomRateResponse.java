package com.kynsoft.finamer.insis.application.command.roomRate.updateResponseImportRoomRate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class RoomRateResponse {
    private UUID innsistBookingId;
    private UUID innsistRoomRateId;
    private UUID invoiceId;
    private List<RoomRateFieldError> errors;
}
