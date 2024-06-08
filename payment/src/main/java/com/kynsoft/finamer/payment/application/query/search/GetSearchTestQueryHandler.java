package com.kynsoft.finamer.payment.application.query.search;


import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.services.ITestService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchTestQueryHandler implements IQueryHandler<GetSearchTestQuery, PaginatedResponse> {
    private final ITestService service;
    
    public GetSearchTestQueryHandler(ITestService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchTestQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
