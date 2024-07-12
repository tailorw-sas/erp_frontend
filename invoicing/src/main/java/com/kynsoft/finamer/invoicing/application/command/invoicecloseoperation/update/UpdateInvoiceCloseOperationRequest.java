package com.kynsoft.finamer.invoicing.application.command.invoicecloseoperation.update;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateInvoiceCloseOperationRequest {

    private Status status;
    private UUID hotel;
    private LocalDate beginDate;
    private LocalDate endDate;
}
