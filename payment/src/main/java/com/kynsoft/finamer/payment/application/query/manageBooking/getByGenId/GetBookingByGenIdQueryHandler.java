package com.kynsoft.finamer.payment.application.query.manageBooking.getByGenId;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.payment.application.query.objectResponse.BookingProjectionResponse;
import com.kynsoft.finamer.payment.domain.dto.projection.booking.BookingProjectionSimple;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import org.springframework.stereotype.Component;

@Component
public class GetBookingByGenIdQueryHandler implements IQueryHandler<GetBookingByGenIdQuery, BookingProjectionResponse>  {

    private final IManageBookingService service;

    public GetBookingByGenIdQueryHandler(IManageBookingService service) {
        this.service = service;
    }

    @Override
    public BookingProjectionResponse handle(GetBookingByGenIdQuery query) {
        BookingProjectionSimple response = service.findSimpleDetailByGenId(query.getId());

        return new BookingProjectionResponse(response);
    }
}
