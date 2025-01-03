package com.kynsoft.finamer.invoicing.application.query.manageBooking.http.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import java.util.UUID;
import lombok.Getter;

@Getter
public class FindBookingHttpByIdQuery  implements IQuery {

    private final UUID id;

    public FindBookingHttpByIdQuery(UUID id) {
        this.id = id;
    }

}
