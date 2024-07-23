package com.kynsoft.finamer.invoicing.application.command.invoicecloseoperation.updateAll;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAllInvoiceCloseOperationRequest {

    private List<UUID> hotels;
    private Status status;
    private LocalDate beginDate;
    private LocalDate endDate;
}
