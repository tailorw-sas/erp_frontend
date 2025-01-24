package com.kynsoft.finamer.insis.application.query.innsistConnectionParamsQuery.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.insis.domain.services.IInnsistConnectionParamsService;
import org.springframework.stereotype.Component;

@Component
public class InnsistConnectionParamsQueryHandler implements IQueryHandler<InnsistConnectionParamsQuery, PaginatedResponse> {

    private final IInnsistConnectionParamsService service;

    public InnsistConnectionParamsQueryHandler(IInnsistConnectionParamsService service){
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(InnsistConnectionParamsQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
