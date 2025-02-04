package com.tailorw.finamer.scheduler.application.query.businessProcess.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchBusinessProcessQueryHandler implements IQueryHandler<GetSearchBusinessProcessQuery, PaginatedResponse> {

    private final IBusinessProcessService service;

    public GetSearchBusinessProcessQueryHandler(IBusinessProcessService service){
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchBusinessProcessQuery query) {
        return service.search(query.getFilter(), query.getPageable());
    }
}
