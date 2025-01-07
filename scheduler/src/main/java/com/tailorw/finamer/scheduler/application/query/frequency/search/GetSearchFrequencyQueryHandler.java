package com.tailorw.finamer.scheduler.application.query.frequency.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.tailorw.finamer.scheduler.domain.services.IFrecuencyService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchFrequencyQueryHandler implements IQueryHandler<GetSearchFrequencyQuery, PaginatedResponse> {

    private final IFrecuencyService frecuencyService;

    public GetSearchFrequencyQueryHandler(IFrecuencyService frecuencyService){
        this.frecuencyService = frecuencyService;
    }

    @Override
    public PaginatedResponse handle(GetSearchFrequencyQuery query) {
        return frecuencyService.search(query.getPageable(), query.getFilter());
    }
}
