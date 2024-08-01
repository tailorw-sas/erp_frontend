package com.kynsoft.finamer.invoicing.application.query.invoiceStatusHistory.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindInvoiceStatusHistoryByIdQuery implements IQuery {

    private final UUID id;

    public FindInvoiceStatusHistoryByIdQuery(UUID id) {
        this.id = id;
    }

}
