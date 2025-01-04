package com.kynsoft.finamer.invoicing.application.query.invoice.http.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import java.util.UUID;
import lombok.Getter;

@Getter
public class FindInvoiceHttpByIdQuery  implements IQuery {

    private final UUID id;

    public FindInvoiceHttpByIdQuery(UUID id) {
        this.id = id;
    }

}
