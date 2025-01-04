package com.kynsoft.finamer.payment.application.query.manageBooking.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageBookingQueryHandler implements IQueryHandler<GetSearchManageBookingQuery, PaginatedResponse> {
    private final IManageBookingService service;
    
    public GetSearchManageBookingQueryHandler(IManageBookingService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageBookingQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
