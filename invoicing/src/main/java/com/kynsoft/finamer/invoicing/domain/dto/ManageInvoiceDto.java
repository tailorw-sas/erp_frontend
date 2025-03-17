package com.kynsoft.finamer.invoicing.domain.dto;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.ImportType;

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
    private String invoiceNumberPrefix;
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

    private String sendStatusError;
    private Double originalAmount;
    private ImportType importType;
    private boolean deleteInvoice;
    private int aging;
    private boolean cloneParent;

    private Long hotelInvoiceNumber;

    public ManageInvoiceDto(UUID id, Long invoiceId, Long invoiceNo, String invoiceNumber, String invoiceNumberPrefix, LocalDateTime invoiceDate, LocalDate dueDate, Boolean isManual,
                            Double invoiceAmount, Double dueAmount, ManageHotelDto hotel,
                            ManageAgencyDto agency, EInvoiceType invoiceType, EInvoiceStatus status,
                            Boolean autoRec, List<ManageBookingDto> bookings, List<ManageAttachmentDto> attachments,
                            Boolean reSend, LocalDate reSendDate, ManageInvoiceTypeDto manageInvoiceType,
                            ManageInvoiceStatusDto manageInvoiceStatus, LocalDateTime createdAt, Boolean isCloned,
                            ManageInvoiceDto parent, Double credits,int aging) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.invoiceNo = invoiceNo;
        this.invoiceNumber = invoiceNumber;
        this.invoiceNumberPrefix = invoiceNumberPrefix;
        this.invoiceDate = invoiceDate;
        this.dueDate = dueDate;
        this.isManual = isManual;
        this.invoiceAmount = invoiceAmount;
        this.dueAmount = dueAmount;
        this.hotel = hotel;
        this.agency = agency;
        this.invoiceType = invoiceType;
        this.status = status;
        this.autoRec = autoRec;
        this.bookings = bookings;
        this.attachments = attachments;
        this.reSend = reSend;
        this.reSendDate = reSendDate;
        this.manageInvoiceType = manageInvoiceType;
        this.manageInvoiceStatus = manageInvoiceStatus;
        this.createdAt = createdAt;
        this.isCloned = isCloned;
        this.parent = parent;
        this.credits = credits;
        this.aging =aging;
    }
}
