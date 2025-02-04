package com.tailorw.finamer.scheduler.application.query.processingDateType.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.tailorw.finamer.scheduler.domain.services.IProcessingDateTypeService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchProcessingDateTypeQueryHandler implements IQueryHandler<GetSearchProcessingDateTypeQuery, PaginatedResponse> {

    private final IProcessingDateTypeService processingDateTypeService;

    public GetSearchProcessingDateTypeQueryHandler(IProcessingDateTypeService processingDateTypeService){
        this.processingDateTypeService = processingDateTypeService;
    }

    @Override
    public PaginatedResponse handle(GetSearchProcessingDateTypeQuery query) {
        return processingDateTypeService.search(query.getPageable(), query.getFilter());
    }
}
