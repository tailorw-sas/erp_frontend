package com.kynsoft.finamer.invoicing.application.command.manageInvoice.update;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UpdateInvoiceRequest {
    private LocalDateTime invoiceDate;
    private UUID agency;
    private UUID invoiceStatus;
    private String employee;

    //private UUID invoiceType;
    //private Status status;
    //private LocalDate reSendDate;
    //private LocalDate dueDate;
    //private Boolean reSend;
    //private Boolean isManual;
    //private Double invoiceAmount;
    //private UUID hotel;
}
