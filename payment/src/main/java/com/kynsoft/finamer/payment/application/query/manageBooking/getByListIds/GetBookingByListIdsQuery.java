package com.kynsoft.finamer.payment.application.query.manageBooking.getByListIds;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

@Getter
public class GetBookingByListIdsQuery  implements IQuery {

    private final Long id;

    public GetBookingByListIdsQuery(Long id) {
        this.id = id;
    }

}
