package com.kynsoft.finamer.payment.application.query.http.invoice.booking;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.http.entity.BookingHttp;
import com.kynsoft.finamer.payment.application.command.invoice.create.CreateInvoiceCommand;
import com.kynsoft.finamer.payment.infrastructure.services.http.BookingHttpService;
import org.springframework.stereotype.Component;

@Component
public class FindBookingByGenIdQueryHandler implements IQueryHandler<FindBookingByGenIdQuery, BookingHttp> {

    private final BookingHttpService service;

    public FindBookingByGenIdQueryHandler(BookingHttpService service) {
        this.service = service;
    }

    @Override
    public BookingHttp handle(FindBookingByGenIdQuery query) {
        BookingHttp response = service.sendGetBookingHttpRequest(query.getId());

        query.getMediator().send(new CreateInvoiceCommand(response));

        return response;
    }

}
