package com.kynsoft.finamer.invoicing.application.command.invoicecloseoperation.create;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateInvoiceCloseOperationRequest {

    private Status status;
    private UUID hotel;
    private LocalDate beginDate;
    private LocalDate endDate;
}
