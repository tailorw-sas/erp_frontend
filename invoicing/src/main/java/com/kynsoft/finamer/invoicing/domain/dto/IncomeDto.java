package com.kynsoft.finamer.invoicing.domain.dto;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IncomeDto {

    private UUID id;
    private Long incomeId;
    private Long invoiceNumber;
    private Status status;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private Boolean manual;
    private Boolean reSend;
    private LocalDate reSendDate;
    private ManageAgencyDto agency;
    private ManageHotelDto hotel;
    private ManageInvoiceTypeDto invoiceType;
    private ManageInvoiceStatusDto invoiceStatus;
    private Double incomeAmount;

    private ManageBookingDto booking;
}
