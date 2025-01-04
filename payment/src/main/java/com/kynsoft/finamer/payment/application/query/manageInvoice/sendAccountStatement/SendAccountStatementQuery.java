package com.kynsoft.finamer.payment.application.query.manageInvoice.sendAccountStatement;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class SendAccountStatementQuery implements IQuery {
    private List<UUID> invoiceIds;
}
