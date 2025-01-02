package com.kynsoft.finamer.invoicing.application.query.manageBooking.getByGenId;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

@Getter
public class FindBookingByGenIdQuery  implements IQuery {

    private final Long id;

    public FindBookingByGenIdQuery(Long id) {
        this.id = id;
    }

}
