package com.kynsoft.finamer.invoicing.application.query.manageBooking.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindBookingByIdQuery  implements IQuery {

    private final UUID id;

    public FindBookingByIdQuery(UUID id) {
        this.id = id;
    }

}
