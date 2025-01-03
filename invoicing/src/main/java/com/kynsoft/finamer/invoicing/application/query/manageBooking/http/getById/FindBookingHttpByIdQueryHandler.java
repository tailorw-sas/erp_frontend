package com.kynsoft.finamer.invoicing.application.query.manageBooking.http.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.http.entity.BookingHttp;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.infrastructure.services.http.helper.BookingImportAutomaticeHelperServiceImpl;
import org.springframework.stereotype.Component;

@Component
public class FindBookingHttpByIdQueryHandler implements IQueryHandler<FindBookingHttpByIdQuery, BookingHttp> {

    private final IManageBookingService service;
    private final BookingImportAutomaticeHelperServiceImpl bookingImportAutomaticeHelperServiceImpl;

    public FindBookingHttpByIdQueryHandler(IManageBookingService service, BookingImportAutomaticeHelperServiceImpl bookingImportAutomaticeHelperServiceImpl) {
        this.service = service;
        this.bookingImportAutomaticeHelperServiceImpl = bookingImportAutomaticeHelperServiceImpl;
    }

    @Override
    public BookingHttp handle(FindBookingHttpByIdQuery query) {
        ManageBookingDto response = service.findById(query.getId());

        return this.bookingImportAutomaticeHelperServiceImpl.createBookingHttp(response);
    }
}
