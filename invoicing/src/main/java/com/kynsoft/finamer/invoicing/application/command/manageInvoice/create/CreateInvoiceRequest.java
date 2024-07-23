package com.kynsoft.finamer.invoicing.application.command.manageInvoice.create;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateInvoiceRequest {
    private UUID id;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private Boolean isManual;
    private Double invoiceAmount;
    private UUID hotel;
    private UUID agency;
    private EInvoiceType invoiceType;

}
