package com.kynsoft.finamer.payment.application.query.manageInvoice.sendAccountStatement;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SendAccountStatementRequest {
    private List<UUID> invoiceIds;
}
