package com.kynsoft.finamer.invoicing.infrastructure.interfacesEntity;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.ImportType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
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
    private final Boolean hasAttachments;
    private final EInvoiceType invoiceType;
    private final EInvoiceStatus invoiceStatus;
    private final String invoiceNumber;
    private final String sendStatusError;
    private final UUID parentId;
    private final Boolean autoRec;
    private final Double originalAmount;
    private final ImportType importType;
    private final Boolean cloneParent;
    private final LocalDate dueDate;
    private final Integer aging;
    private final ManageInvoiceHotelProjection hotel;
    private final ManageInvoiceAgencyProjection agency;
    private final ManageInvoiceStatusProjection manageInvoiceStatus;
    private final ManageInvoiceTypeProjection manageInvoiceType;
    private final boolean isCloseOperation;
}