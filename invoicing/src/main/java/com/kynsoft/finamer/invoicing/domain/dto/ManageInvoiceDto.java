package com.kynsoft.finamer.invoicing.domain.dto;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageBooking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageInvoiceDto {
    private UUID id;
    private Long invoice_id;
    private Long invoiceNo;
    private String invoiceNumber;
    private LocalDateTime invoiceDate;
    private LocalDateTime dueDate;
    private Boolean isManual;
    private Double invoiceAmount;
    private ManageHotelDto hotel;
    private ManageAgencyDto agency;
    private EInvoiceType invoiceType;
    private EInvoiceStatus status;
    private Boolean autoRec;
    private List<ManageBookingDto> bookings;
    private List<ManageAttachmentDto> attachments;
}
