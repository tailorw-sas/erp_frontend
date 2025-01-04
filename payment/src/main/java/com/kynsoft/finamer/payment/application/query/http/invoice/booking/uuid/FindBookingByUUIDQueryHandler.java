package com.kynsoft.finamer.payment.application.query.http.invoice.booking.uuid;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.http.entity.BookingHttp;
import com.kynsoft.finamer.payment.infrastructure.services.http.BookingHttpUUIDService;
import com.kynsoft.finamer.payment.infrastructure.services.http.helper.BookingImportAutomaticeHelperServiceImpl;
import org.springframework.stereotype.Component;

@Component
public class FindBookingByUUIDQueryHandler implements IQueryHandler<FindBookingByUUIDQuery, BookingHttp> {

    private final BookingHttpUUIDService service;
    private final BookingImportAutomaticeHelperServiceImpl bookingImportAutomaticeHelperServiceImpl;

    public FindBookingByUUIDQueryHandler(BookingHttpUUIDService service,
                                         BookingImportAutomaticeHelperServiceImpl bookingImportAutomaticeHelperServiceImpl) {
        this.service = service;
        this.bookingImportAutomaticeHelperServiceImpl = bookingImportAutomaticeHelperServiceImpl;
    }

    @Override
    public BookingHttp handle(FindBookingByUUIDQuery query) {
        BookingHttp response = service.sendGetBookingHttpRequest(query.getId());
        this.bookingImportAutomaticeHelperServiceImpl.createInvoice(response);

        return response;
    }

}
