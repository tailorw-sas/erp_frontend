package com.kynsoft.finamer.invoicing.application.command.manageInvoice.reconcileManual;

import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.infrastructure.utils.InvoiceUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class ReconcileManualErrorResponse {
    private String invoiceId;
    private ReconcileManualHotelResponse hotel;
    private String invoiceNo;
    private ReconcileManualAgencyResponse agency;
    private LocalDate invoiceDate;
    private Double invoiceAmount;
    private String message;
    private EInvoiceStatus status;

    public ReconcileManualErrorResponse(ManageInvoiceDto invoiceDto, String message){
        this.invoiceId = invoiceDto.getInvoiceId().toString();
        this.hotel = new ReconcileManualHotelResponse(invoiceDto.getHotel());
        this.invoiceNo = InvoiceUtils.deleteHotelInfo(invoiceDto.getInvoiceNumber());
        this.agency = new ReconcileManualAgencyResponse(invoiceDto.getAgency());
        this.invoiceDate = invoiceDto.getInvoiceDate().toLocalDate();
        this.invoiceAmount = invoiceDto.getInvoiceAmount();
        this.message = message;
        this.status = invoiceDto.getStatus();
    }
}
