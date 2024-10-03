package com.kynsoft.finamer.invoicing.application.query.manageInvoiceStatus.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class FindManageInvoiceStatusByIdQuery implements IQuery {

    private final UUID id;
}
