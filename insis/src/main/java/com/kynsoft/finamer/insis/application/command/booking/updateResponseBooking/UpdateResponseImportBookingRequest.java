package com.kynsoft.finamer.insis.application.command.booking.updateResponseBooking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateResponseImportBookingRequest {
    private UUID importProcessId;
    private List<RoomRateResponse> responses;
}
