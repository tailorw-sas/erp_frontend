package com.kynsof.identity.application.query.businessModule.search;

import com.kynsof.identity.domain.interfaces.service.IBusinessModuleService;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import org.springframework.stereotype.Component;

@Component
public class GetSearchBusinessModuleQueryHandler implements IQueryHandler<GetSearchBusinessModuleQuery, PaginatedResponse>{
    private final IBusinessModuleService service;
    
    public GetSearchBusinessModuleQueryHandler(IBusinessModuleService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchBusinessModuleQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
