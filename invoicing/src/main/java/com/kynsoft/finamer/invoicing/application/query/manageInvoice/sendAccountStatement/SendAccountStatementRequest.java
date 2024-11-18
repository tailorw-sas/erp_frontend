package com.kynsoft.finamer.invoicing.application.query.manageInvoice.sendAccountStatement;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SendAccountStatementRequest {
    private List<UUID> invoiceIds;
    private UUID employee;
}
