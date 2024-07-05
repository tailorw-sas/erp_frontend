package com.kynsoft.finamer.invoicing.application.query.manageBooking.search;


import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchBookingQueryHandler implements IQueryHandler<GetSearchBookingQuery, PaginatedResponse> {
    private final IManageBookingService service;
    
    public GetSearchBookingQueryHandler(IManageBookingService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchBookingQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
