package com.kynsoft.finamer.invoicing.infrastructure.interfacesEntity;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.ImportType;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ManageInvoiceSearchProjection {
    UUID getId();
    Long getInvoiceId();
    Boolean getIsManual();
    Long getInvoiceNo();
    Double getInvoiceAmount();
    Double getDueAmount();
    LocalDateTime getInvoiceDate();
    ManageInvoiceHotelProjection getHotel();
    ManageInvoiceAgencyProjection getAgency();
    ManageInvoiceStatusProjection getInvoiceStatus();
    Boolean getHasAttachments();
    String getStatus();
    //Boolean getIsInCloseOperation();
    EInvoiceType getInvoiceType();
    String getInvoiceNumber();
    ManageInvoiceTypeProjection getManageInvoiceType();
    String getSendStatusError();
    UUID getParentId();
    Boolean getAutoRec();
     Double getOriginalAmount();
    ImportType getImportType();
    boolean getCloneParent();
    Integer getAging();

    default Boolean getIsInCloseOperation() {
        return true;
    }

   // boolean getIsCloneParent();
}

