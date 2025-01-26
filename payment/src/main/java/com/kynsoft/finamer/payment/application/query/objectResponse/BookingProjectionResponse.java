package com.kynsoft.finamer.payment.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.payment.domain.dto.projection.booking.BookingProjectionSimple;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookingProjectionResponse implements IResponse {

    private UUID bookingId;
    private Long bookingGenId;
    private double bookingAmountBalance;
    //Datos de la invoice para ese booking.
    private EInvoiceType invoiceType;
    private UUID invoiceHotel;
    private String clientName;

    public BookingProjectionResponse(BookingProjectionSimple dto) {
        this.bookingId = dto.getBookingId();
        this.bookingGenId = dto.getBookingGenId();
        this.bookingAmountBalance = dto.getBookingAmountBalance();
        this.invoiceType = dto.getInvoiceType();
        this.invoiceHotel = dto.getInvoiceHotel();
        this.clientName = dto.getClientName();
    }

}
