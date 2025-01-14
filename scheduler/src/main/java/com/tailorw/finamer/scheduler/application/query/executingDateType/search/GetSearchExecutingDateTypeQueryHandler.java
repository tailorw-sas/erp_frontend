package com.tailorw.finamer.scheduler.application.query.executingDateType.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.tailorw.finamer.scheduler.domain.services.IExecutionDateTypeService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchExecutingDateTypeQueryHandler implements IQueryHandler<GetSearchExecutingDateTypeQuery, PaginatedResponse> {

    private final IExecutionDateTypeService executionDateTypeService;

    public GetSearchExecutingDateTypeQueryHandler(IExecutionDateTypeService executionDateTypeService){
        this.executionDateTypeService = executionDateTypeService;
    }

    @Override
    public PaginatedResponse handle(GetSearchExecutingDateTypeQuery query) {
        return executionDateTypeService.search(query.getPageable(), query.getFilter());
    }
}
