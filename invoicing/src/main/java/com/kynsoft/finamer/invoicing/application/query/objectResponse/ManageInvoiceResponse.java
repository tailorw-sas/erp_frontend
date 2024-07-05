package com.kynsoft.finamer.invoicing.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceTypeDto;
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
public class ManageInvoiceResponse implements IResponse {
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
    private boolean autoRec;

    public ManageInvoiceResponse(ManageInvoiceDto dto) {
        this.id = dto.getId();
        this.invoice_id = dto.getInvoice_id();
        this.invoiceNumber = dto.getInvoiceNumber();
        this.invoiceDate = dto.getInvoiceDate();
        this.isManual = dto.getIsManual();
        this.invoiceAmount = dto.getInvoiceAmount();
        this.hotel = dto.getHotel();
        this.agency = dto.getAgency();
        this.invoiceType = dto.getInvoiceType();
        this.status = dto.getStatus();
        this.autoRec = dto.getAutoRec();
    }
}
