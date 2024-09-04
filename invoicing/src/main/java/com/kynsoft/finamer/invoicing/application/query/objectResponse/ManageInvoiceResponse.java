package com.kynsoft.finamer.invoicing.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageInvoiceResponse implements IResponse {
    private UUID id;
    private Long invoiceId;
    private Long invoiceNo;
    private String invoiceNumber;
    private LocalDateTime invoiceDate;
    private Boolean isManual;
    private Double invoiceAmount;
    private Double dueAmount;
    private ManageHotelDto hotel;
    private ManageAgencyDto agency;
    private EInvoiceType invoiceType;
    private EInvoiceStatus status;
    private boolean autoRec;
    private boolean hasAttachments;
    private Boolean reSend;
    private Boolean isCloned;
    private LocalDate reSendDate;
    private LocalDate dueDate;
    private ManageInvoiceTypeResponse manageInvoiceType;
    private ManageInvoiceStatusResponse manageInvoiceStatus;
    private LocalDateTime createdAt;
    private Boolean isInCloseOperation = true;

    private ManageInvoiceDto parent;
    private Double credits;

    public ManageInvoiceResponse(ManageInvoiceDto dto) {
        this.id = dto.getId();
        this.invoiceId = dto.getInvoiceId();
        this.invoiceNumber = this.deleteHotelInfo(dto.getInvoiceNumber());
        this.invoiceDate = dto.getInvoiceDate();
        this.isManual = dto.getIsManual();
        this.invoiceAmount = dto.getInvoiceAmount();
        this.dueAmount = dto.getDueAmount();
        this.hotel = dto.getHotel();
        this.agency = dto.getAgency();
        this.invoiceType = dto.getInvoiceType() != null ? dto.getInvoiceType() : EInvoiceType.INVOICE;
        this.status = dto.getStatus() != null ? dto.getStatus() : EInvoiceStatus.PROCECSED;
        this.autoRec = dto.getAutoRec() != null ? dto.getAutoRec() : false;
        this.hasAttachments = dto.getAttachments() != null && dto.getAttachments().size() > 0;
        this.reSend = dto.getReSend();
        this.reSendDate = dto.getReSendDate();
        this.manageInvoiceType = dto.getManageInvoiceType() != null ? new ManageInvoiceTypeResponse(dto.getManageInvoiceType()) : null;
        this.manageInvoiceStatus = dto.getManageInvoiceStatus() != null ? new ManageInvoiceStatusResponse(dto.getManageInvoiceStatus()) : null;
        this.createdAt = dto.getCreatedAt();
        this.dueDate = dto.getDueDate();
        this.isCloned = dto.getIsCloned();
        this.parent = dto.getParent();
        this.invoiceNo = dto.getInvoiceNo();
        this.credits = dto.getCredits();
    }

    private String deleteHotelInfo(String input) {
        return input.replaceAll("-(.*?)-", "-");
    }

}
