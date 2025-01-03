package com.kynsoft.finamer.payment.application.query.http.invoice.booking.uuid;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.http.entity.BookingHttp;
import com.kynsoft.finamer.payment.application.command.invoice.create.CreateInvoiceCommand;
import com.kynsoft.finamer.payment.infrastructure.services.http.BookingHttpUUIDService;
import org.springframework.stereotype.Component;

@Component
public class FindBookingByUUIDQueryHandler implements IQueryHandler<FindBookingByUUIDQuery, BookingHttp> {

    private final BookingHttpUUIDService service;

    public FindBookingByUUIDQueryHandler(BookingHttpUUIDService service) {
        this.service = service;
    }

    @Override
    public BookingHttp handle(FindBookingByUUIDQuery query) {
        BookingHttp response = service.sendGetBookingHttpRequest(query.getId());

        query.getMediator().send(new CreateInvoiceCommand(response));

        return response;
    }

}
