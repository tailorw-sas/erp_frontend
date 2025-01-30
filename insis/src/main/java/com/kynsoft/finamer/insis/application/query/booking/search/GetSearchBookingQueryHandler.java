package com.kynsoft.finamer.insis.application.query.booking.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.insis.domain.services.IBookingService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchBookingQueryHandler implements IQueryHandler<GetSearchBookingQuery, PaginatedResponse> {

    private final IBookingService service;

    public GetSearchBookingQueryHandler(IBookingService service){
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchBookingQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
