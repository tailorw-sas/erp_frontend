package com.kynsoft.finamer.invoicing.application.query.manageHotel.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchHotelQueryHandler implements IQueryHandler<GetSearchHotelQuery, PaginatedResponse> {
    private final IManageHotelService service;
    
    public GetSearchHotelQueryHandler(IManageHotelService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchHotelQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
