package com.kynsoft.finamer.invoicing.application.command.manageInvoice.update;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UpdateInvoiceRequest {
    private LocalDateTime invoiceDate;
    private UUID agency;
    private String employee;
    private EInvoiceStatus status;
}
