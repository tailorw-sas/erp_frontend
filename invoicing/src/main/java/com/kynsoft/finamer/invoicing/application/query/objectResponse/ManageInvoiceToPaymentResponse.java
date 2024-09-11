package com.kynsoft.finamer.invoicing.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageInvoiceToPaymentResponse implements IResponse {
    private UUID id;
    private Long invoiceId;
    private Long invoiceNo;
    private String invoiceNumber;
    private Double invoiceAmount;
    private Double dueAmount;
    private ManageHotelDto hotel;
    private ManageAgencyDto agency;
    private EInvoiceType invoiceType;
    private EInvoiceStatus status;

    private List<ManageBookingDto> bookings;

    public ManageInvoiceToPaymentResponse(ManageInvoiceDto dto) {
        this.id = dto.getId();
        this.invoiceId = dto.getInvoiceId();
        this.invoiceNumber = this.deleteHotelInfo(dto.getInvoiceNumber());
        this.invoiceAmount = dto.getInvoiceAmount();
        this.dueAmount = dto.getDueAmount();
        this.hotel = dto.getHotel();
        this.agency = dto.getAgency();
        this.invoiceType = dto.getInvoiceType() != null ? dto.getInvoiceType() : EInvoiceType.INVOICE;
        this.invoiceNo = dto.getInvoiceNo();
        this.bookings = dto.getBookings();
        this.status = dto.getStatus();
    }

    private String deleteHotelInfo(String input) {
        return input.replaceAll("-(.*?)-", "-");
    }

}
