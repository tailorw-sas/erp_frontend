package com.kynsoft.finamer.insis.application.command.booking.updateResponseBooking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class RoomRateResponse {
    private UUID innsistBookingId;
    private UUID innsistRoomRateId;
    private UUID invoiceId;
    private String msg;
}
