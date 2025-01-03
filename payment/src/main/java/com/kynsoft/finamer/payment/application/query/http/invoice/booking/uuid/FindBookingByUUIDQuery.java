package com.kynsoft.finamer.payment.application.query.http.invoice.booking.uuid;

import com.kynsof.share.core.domain.bus.query.IQuery;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import java.util.UUID;
import lombok.Getter;

@Getter
public class FindBookingByUUIDQuery  implements IQuery {

    private final UUID id;
    private final IMediator mediator;

    public FindBookingByUUIDQuery(UUID id, IMediator mediator) {
        this.id = id;
        this.mediator = mediator;
    }

}
