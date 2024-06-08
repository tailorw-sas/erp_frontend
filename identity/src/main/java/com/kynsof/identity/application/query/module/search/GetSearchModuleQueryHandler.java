package com.kynsof.identity.application.query.module.search;

import com.kynsof.identity.domain.interfaces.service.IModuleService;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import org.springframework.stereotype.Component;

@Component
public class GetSearchModuleQueryHandler implements IQueryHandler<GetSearchModuleQuery, PaginatedResponse>{
    private final IModuleService service;
    
    public GetSearchModuleQueryHandler(IModuleService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchModuleQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
