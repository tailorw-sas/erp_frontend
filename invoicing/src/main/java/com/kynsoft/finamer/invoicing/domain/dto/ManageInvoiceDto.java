package com.kynsoft.finamer.invoicing.domain.dto;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageInvoiceDto {
    private UUID id;
    private Long invoiceId;
    private Long invoiceNo;
    private String invoiceNumber;
    private LocalDateTime invoiceDate;
    private LocalDate dueDate;
    private Boolean isManual;
    private Double invoiceAmount;
    private Double dueAmount;
    private ManageHotelDto hotel;
    private ManageAgencyDto agency;
    private EInvoiceType invoiceType;
    private EInvoiceStatus status;
    private Boolean autoRec;
    private List<ManageBookingDto> bookings;
    private List<ManageAttachmentDto> attachments;

    private Boolean reSend;
    private LocalDate reSendDate;
    private ManageInvoiceTypeDto manageInvoiceType;
    private ManageInvoiceStatusDto manageInvoiceStatus;
    private LocalDateTime createdAt;

    private Boolean isCloned;
    private ManageInvoiceDto parent;
    private Double credits;

}
