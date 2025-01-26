package com.kynsoft.finamer.payment.application.query.manageBooking.getByGenId;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

@Getter
public class GetBookingByGenIdQuery  implements IQuery {

    private final long id;

    public GetBookingByGenIdQuery(long id) {
        this.id = id;
    }

}
