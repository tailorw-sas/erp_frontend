package com.kynsoft.finamer.invoicing.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.invoicing.domain.dto.IncomeDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IncomeResponse implements IResponse {

    private UUID id;
    private Long incomeId;
    private Status status;
    private LocalDate invoiceDate;
    private Boolean manual;
    private ManageAgencyResponse agency;
    private ManageHotelResponse hotel;
    private ManageInvoiceTypeResponse invoiceType;
    private ManageInvoiceStatusResponse invoiceStatus;
    private Double incomeAmount;
    private Long invoiceNumber;
    private LocalDate dueDate;
    private Boolean reSend;
    private LocalDate reSendDate;

    public IncomeResponse(IncomeDto dto) {
        this.id = dto.getId();
        this.incomeId = dto.getIncomeId();
        this.status = dto.getStatus();
        this.invoiceDate = dto.getInvoiceDate();
        this.manual = dto.getManual();
        this.agency = dto.getAgency() != null ? new ManageAgencyResponse(dto.getAgency()) : null;
        this.hotel = dto.getHotel() != null ? new ManageHotelResponse(dto.getHotel()) : null;
        this.invoiceType = dto.getInvoiceType() != null ? new ManageInvoiceTypeResponse(dto.getInvoiceType()) : null;
        this.invoiceStatus = dto.getInvoiceStatus() != null ? new ManageInvoiceStatusResponse(dto.getInvoiceStatus()) : null;
        this.incomeAmount = dto.getIncomeAmount();
        this.invoiceNumber = dto.getInvoiceNumber();
        this.dueDate = dto.getDueDate();
        this.reSend = dto.getReSend();
        this.reSendDate = dto.getReSendDate();
    }

}
