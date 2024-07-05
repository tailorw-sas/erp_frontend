package com.kynsoft.finamer.invoicing.domain.dto;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageInvoiceDto {
    private UUID id;
    private Long invoice_id;
    private Long invoiceNumber;
    private LocalDateTime invoiceDate;
    private Boolean isManual;
    private Double invoiceAmount;
    private ManageHotelDto hotel;
    private ManageAgencyDto agency;
    private ManageInvoiceTypeDto invoiceType;
    private ManageInvoiceStatusDto status;
    private Boolean autoRec;
}
