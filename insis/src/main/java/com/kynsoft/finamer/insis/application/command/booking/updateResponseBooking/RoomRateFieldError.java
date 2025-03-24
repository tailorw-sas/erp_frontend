package com.kynsoft.finamer.insis.application.command.booking.updateResponseBooking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomRateFieldError {
    private String field;
    private String messageError;
}
