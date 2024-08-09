package com.kynsoft.report.applications.query.dbconection.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.IDBConectionService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchDBConectionQueryHandler implements IQueryHandler<GetSearchDBConectionQuery, PaginatedResponse> {

    private final IDBConectionService service;

    public GetSearchDBConectionQueryHandler(IDBConectionService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchDBConectionQuery query) {
        return this.service.search(query.getPageable(),query.getFilter());
    }
}
