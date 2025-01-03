package com.kynsoft.finamer.payment.application.query.http.invoice.invoice.uuid;

import com.kynsof.share.core.domain.bus.query.IQuery;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import java.util.UUID;
import lombok.Getter;

@Getter
public class FindInvoiceByUUIDQuery  implements IQuery {

    private final UUID id;
    private final IMediator mediator;

    public FindInvoiceByUUIDQuery(UUID id, IMediator mediator) {
        this.id = id;
        this.mediator = mediator;
    }

}
