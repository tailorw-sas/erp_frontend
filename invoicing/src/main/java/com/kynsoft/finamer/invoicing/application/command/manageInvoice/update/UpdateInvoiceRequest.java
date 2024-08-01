package com.kynsoft.finamer.invoicing.application.command.manageInvoice.update;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class UpdateInvoiceRequest {
    private LocalDate invoiceDate;
    private Boolean isManual;
    private Double invoiceAmount;
    private UUID hotel;
    private UUID agency;
    private UUID invoiceType;
    private Status status;
    private LocalDate reSendDate;
    private LocalDate dueDate;
    private Boolean reSend;
    private String employee;
}
