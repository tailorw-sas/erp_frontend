package com.kynsoft.finamer.insis.application.query.roomRate.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.insis.domain.services.IRoomRateService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchRoomRateQueryHandler implements IQueryHandler<GetSearchRoomRateQuery, PaginatedResponse> {

    private final IRoomRateService service;

    public GetSearchRoomRateQueryHandler(IRoomRateService service){
        this.service = service;
    }
    @Override
    public PaginatedResponse handle(GetSearchRoomRateQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
