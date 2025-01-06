package com.tailorw.finamer.scheduler.application.query.businessProcessScheduler.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessSchedulerService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchBusinessProcessSchedulerQueryHandler implements IQueryHandler<GetSearchBusinessProcessSchedulerQuery, PaginatedResponse> {

    private final IBusinessProcessSchedulerService service;

    public GetSearchBusinessProcessSchedulerQueryHandler(IBusinessProcessSchedulerService service){
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchBusinessProcessSchedulerQuery query) {
        return service.search(query.getFilter(), query.getPageable());
    }
}
