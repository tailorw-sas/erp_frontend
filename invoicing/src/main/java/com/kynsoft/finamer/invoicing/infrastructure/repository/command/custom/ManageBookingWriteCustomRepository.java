package com.kynsoft.finamer.invoicing.infrastructure.repository.command.custom;

import com.kynsoft.finamer.invoicing.infrastructure.identity.Booking;

public interface ManageBookingWriteCustomRepository {

    void insert(Booking booking);
}
