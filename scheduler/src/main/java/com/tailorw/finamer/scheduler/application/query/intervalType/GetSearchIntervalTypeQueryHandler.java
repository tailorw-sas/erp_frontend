package com.tailorw.finamer.scheduler.application.query.intervalType;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.tailorw.finamer.scheduler.domain.services.IIntervalTypeService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchIntervalTypeQueryHandler implements IQueryHandler<GetSearchIntervalTypeQuery, PaginatedResponse> {

    private final IIntervalTypeService intervalTypeService;

    public GetSearchIntervalTypeQueryHandler(IIntervalTypeService intervalTypeService){
        this.intervalTypeService = intervalTypeService;
    }

    @Override
    public PaginatedResponse handle(GetSearchIntervalTypeQuery query) {
        return intervalTypeService.search(query.getPageable(), query.getFilter());
    }
}
