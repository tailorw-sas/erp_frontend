package com.kynsoft.finamer.invoicing.infrastructure.interfacesEntity;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.ImportType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ManageInvoiceSearchProjection {
    private final UUID id;
    private final Long invoiceId;
    private final Boolean isManual;
    private final Long invoiceNo;
    private final Double invoiceAmount;
    private final Double dueAmount;
    private final LocalDateTime invoiceDate;
    private final ManageInvoiceHotelProjection hotel;
    private final ManageInvoiceAgencyProjection agency;
    private final ManageInvoiceStatusProjection manageInvoiceStatus;
    private final Boolean hasAttachments;
    private final EInvoiceType invoiceType;
    private EInvoiceStatus invoiceStatus;
    private final String invoiceNumber;
    private final ManageInvoiceTypeProjection manageInvoiceType;
    private final String sendStatusError;
    private final UUID parentId;
    private final Boolean autoRec;
    private final Double originalAmount;
    private final ImportType importType;
    private final Boolean cloneParent;
    private final Integer aging;
    private boolean isCloseOperation;
    private LocalDate dueDate;

    public ManageInvoiceSearchProjection(
            UUID id,
            Long invoiceId,
            Boolean isManual,
            Long invoiceNo,
            Double invoiceAmount,
            Double dueAmount,
            LocalDateTime invoiceDate,
            ManageInvoiceHotelProjection hotel,
            ManageInvoiceAgencyProjection agency,
            ManageInvoiceStatusProjection manageInvoiceStatus,
            Boolean hasAttachments,
            EInvoiceType invoiceType,
            EInvoiceStatus invoiceStatus,
            String invoiceNumber,
            ManageInvoiceTypeProjection manageInvoiceType,
            String sendStatusError,
            UUID parentId,
            Boolean autoRec,
            Double originalAmount,
            ImportType importType,
            Boolean cloneParent,
            Integer aging,
            Boolean isCloseOperation,
            LocalDate dueDate
    ) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.isManual = isManual;
        this.invoiceNo = invoiceNo;
        this.invoiceAmount = invoiceAmount;
        this.dueAmount = dueAmount;
        this.invoiceDate = invoiceDate;
        this.hotel = hotel;
        this.agency = agency;
        this.manageInvoiceStatus = manageInvoiceStatus;
        this.invoiceStatus= invoiceStatus;
        this.hasAttachments = hasAttachments;
        this.invoiceType = invoiceType;
        this.invoiceNumber = invoiceNumber;
        this.manageInvoiceType = manageInvoiceType;
        this.sendStatusError = sendStatusError;
        this.parentId = parentId;
        this.autoRec = autoRec;
        this.originalAmount = originalAmount;
        this.importType = importType;
        this.cloneParent = cloneParent;
        this.aging = aging;
        this.isCloseOperation = isCloseOperation;
        this.dueDate = dueDate;
    }


    // Agrega getters para todos los campos si es necesario.
}