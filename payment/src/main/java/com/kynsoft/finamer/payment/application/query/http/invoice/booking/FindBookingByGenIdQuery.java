package com.kynsoft.finamer.payment.application.query.http.invoice.booking;

import com.kynsof.share.core.domain.bus.query.IQuery;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import lombok.Getter;

@Getter
public class FindBookingByGenIdQuery  implements IQuery {

    private final Long id;
    private final IMediator mediator;

    public FindBookingByGenIdQuery(Long id, IMediator mediator) {
        this.id = id;
        this.mediator = mediator;
    }

}
