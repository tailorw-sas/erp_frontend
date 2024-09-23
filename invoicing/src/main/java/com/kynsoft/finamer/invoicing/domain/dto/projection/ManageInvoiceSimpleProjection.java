package com.kynsoft.finamer.invoicing.domain.dto.projection;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


public interface ManageInvoiceSimpleProjection {
    UUID getId();
    Long getInvoiceId();
    Boolean getIsManual();
    Long getInvoiceNo();
    Double getInvoiceAmount();
    Double getDueAmount();
    LocalDateTime getInvoiceDate();
    ManageHotelSimpleProjection getHotel();
    ManageAgencySimpleProjection getAgency();
   // Boolean getHasAttachments();
    EInvoiceStatus getInvoiceStatus();
    String getStatus();
    ManageInvoiceStatusProjection getManageInvoiceStatus();
    LocalDate getCreatedAt();
    EInvoiceType getInvoiceType();
    String getInvoiceNumber();
}
