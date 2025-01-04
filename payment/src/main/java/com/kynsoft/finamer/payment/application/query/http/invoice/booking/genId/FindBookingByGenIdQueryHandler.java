package com.kynsoft.finamer.payment.application.query.http.invoice.booking.genId;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.http.entity.BookingHttp;
import com.kynsoft.finamer.payment.infrastructure.services.http.BookingHttpGenIdService;
import com.kynsoft.finamer.payment.infrastructure.services.http.helper.BookingImportAutomaticeHelperServiceImpl;
import org.springframework.stereotype.Component;

@Component
public class FindBookingByGenIdQueryHandler implements IQueryHandler<FindBookingByGenIdQuery, BookingHttp> {

    private final BookingHttpGenIdService service;
    private final BookingImportAutomaticeHelperServiceImpl bookingImportAutomaticeHelperServiceImpl;

    public FindBookingByGenIdQueryHandler(BookingHttpGenIdService service,
                                          BookingImportAutomaticeHelperServiceImpl bookingImportAutomaticeHelperServiceImpl) {
        this.service = service;
        this.bookingImportAutomaticeHelperServiceImpl = bookingImportAutomaticeHelperServiceImpl;
    }

    @Override
    public BookingHttp handle(FindBookingByGenIdQuery query) {
        BookingHttp response = service.sendGetBookingHttpRequest(query.getId());
        this.bookingImportAutomaticeHelperServiceImpl.createInvoice(response);

        return response;
    }

}
