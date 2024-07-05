package com.kynsoft.finamer.invoicing.application.command.manageBooking.createMany;

import com.kynsoft.finamer.invoicing.application.command.manageBooking.create.CreateBookingRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateManyBookingRequest {

    private List<CreateBookingRequest> bookings;
}
