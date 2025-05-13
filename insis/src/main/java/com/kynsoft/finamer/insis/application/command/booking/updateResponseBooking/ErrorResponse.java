package com.kynsoft.finamer.insis.application.command.booking.updateResponseBooking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private UUID bookingId;
    private String errorMessage;
}
