package com.kynsoft.finamer.insis.application.command.booking.importBooking;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ImportBookingRequest {
    public UUID id;
    public UUID userId;
    public List<UUID> bookings;
}
