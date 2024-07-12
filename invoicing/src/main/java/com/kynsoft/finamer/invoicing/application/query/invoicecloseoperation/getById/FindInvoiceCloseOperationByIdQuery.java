package com.kynsoft.finamer.invoicing.application.query.invoicecloseoperation.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindInvoiceCloseOperationByIdQuery  implements IQuery {

    private final UUID id;

    public FindInvoiceCloseOperationByIdQuery(UUID id) {
        this.id = id;
    }

}
