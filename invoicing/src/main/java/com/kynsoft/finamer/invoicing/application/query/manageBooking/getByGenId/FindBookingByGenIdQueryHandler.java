package com.kynsoft.finamer.invoicing.application.query.manageBooking.getByGenId;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageBookingResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import org.springframework.stereotype.Component;

@Component
public class FindBookingByGenIdQueryHandler implements IQueryHandler<FindBookingByGenIdQuery, ManageBookingResponse>  {

    private final IManageBookingService service;

    public FindBookingByGenIdQueryHandler(IManageBookingService service) {
        this.service = service;
    }

    @Override
    public ManageBookingResponse handle(FindBookingByGenIdQuery query) {
        ManageBookingDto response = service.findBookingId(query.getId());

        return new ManageBookingResponse(response);
    }
}
