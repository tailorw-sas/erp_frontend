package com.kynsoft.finamer.insis.application.query.objectResponse.roomRate;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageHotel.ManageHotelResponse;
import com.kynsoft.finamer.insis.domain.dto.CountRoomRateByHotelAndInvoiceDateDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
public class CountRoomRateByHotelAndInvoiceDateResponse implements IResponse, Serializable {
    private ManageHotelResponse hotel;
    private LocalDate invoiceDate;
    private Long total;

    public CountRoomRateByHotelAndInvoiceDateResponse(CountRoomRateByHotelAndInvoiceDateDto dto){
        this.hotel = Objects.nonNull(dto.getHotel()) ? new ManageHotelResponse(dto.getHotel()) : null;
        this.invoiceDate = dto.getInvoiceDate();
        this.total = dto.getTotal();
    }
}
