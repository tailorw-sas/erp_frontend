package com.kynsoft.finamer.invoicing.application.query.manageRoomRate.search;


import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomRateService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchRoomRateQueryHandler implements IQueryHandler<GetSearchRoomRateQuery, PaginatedResponse> {
    private final IManageRoomRateService service;
    
    public GetSearchRoomRateQueryHandler(IManageRoomRateService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchRoomRateQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
