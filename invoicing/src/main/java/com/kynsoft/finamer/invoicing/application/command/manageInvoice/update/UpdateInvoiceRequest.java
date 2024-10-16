package com.kynsoft.finamer.invoicing.application.command.manageInvoice.update;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UpdateInvoiceRequest {
    private String employee;
    private LocalDateTime invoiceDate;
    private UUID agency;
    private UUID invoiceStatus;
}
