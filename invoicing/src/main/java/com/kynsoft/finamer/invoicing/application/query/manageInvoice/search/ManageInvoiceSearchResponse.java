package com.kynsoft.finamer.invoicing.application.query.manageInvoice.search;

import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageAgencyResponse;
import com.kynsoft.finamer.invoicing.domain.dto.projection.ManageInvoiceSimpleProjection;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ManageInvoiceSearchResponse {
    private UUID id;
    private Long invoiceId;
    private Boolean isManual;
    private Long invoiceNo;
    private Double invoiceAmount;
    private Double dueAmount;
    private LocalDateTime invoiceDate;
    private ManageInvoiceHotelResponse hotel;
    private  ManageInvoiceAgencyResponse agency;
    private ManageInvoiceStatusResponse invoiceStatus;
    private Boolean hasAttachments;
    private EInvoiceStatus status;
    private Boolean isInCloseOperation;
    private EInvoiceType invoiceType;

    public ManageInvoiceSearchResponse(ManageInvoiceSimpleProjection projection, Boolean isHasAttachments, Boolean isInCloseOperation) {
        this.id = projection.getId();
        this.invoiceId = projection.getInvoiceId();
        this.isManual = projection.getIsManual();
        this.invoiceNo = projection.getInvoiceNo();
        this.invoiceAmount = projection.getInvoiceAmount();
        this.dueAmount = projection.getDueAmount();
        this.invoiceDate = projection.getInvoiceDate();
        this.hotel = new ManageInvoiceHotelResponse(projection.getHotel());
        this.agency = new ManageInvoiceAgencyResponse(projection.getAgency());
        this.invoiceStatus = new ManageInvoiceStatusResponse(projection.getManageInvoiceStatus());
        this.hasAttachments = isHasAttachments;
        this.status = projection.getInvoiceStatus();
        this.isInCloseOperation = isInCloseOperation;
        this.invoiceType = projection.getInvoiceType();
    }
}
