package com.kynsoft.finamer.invoicing.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HotelInvoiceNumberSequenceResponse implements IResponse {

    private UUID id;
    private Long invoiceNo;
    private ManageHotelResponse hotel;
    private ManageTraidingCompaniesResponse manageTradingCompanies;
    private EInvoiceType invoiceType;

    public HotelInvoiceNumberSequenceResponse(HotelInvoiceNumberSequenceDto dto) {
        this.id = dto.getId();
        this.invoiceNo = dto.getInvoiceNo();
        this.hotel = dto.getHotel() != null ? new ManageHotelResponse(dto.getHotel()) : null;
        this.manageTradingCompanies = dto.getManageTradingCompanies() != null ? new ManageTraidingCompaniesResponse(dto.getManageTradingCompanies()) : null;
        this.invoiceType = dto.getInvoiceType();
    }

}
