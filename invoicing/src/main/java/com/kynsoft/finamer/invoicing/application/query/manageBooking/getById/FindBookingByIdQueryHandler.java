package com.kynsoft.finamer.invoicing.application.query.manageBooking.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageBookingResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import org.springframework.stereotype.Component;

@Component
public class FindBookingByIdQueryHandler implements IQueryHandler<FindBookingByIdQuery, ManageBookingResponse>  {

    private final IManageBookingService service;

    public FindBookingByIdQueryHandler(IManageBookingService service) {
        this.service = service;
    }

    @Override
    public ManageBookingResponse handle(FindBookingByIdQuery query) {
        ManageBookingDto response = service.findById(query.getId());

        return new ManageBookingResponse(response);
    }
}
