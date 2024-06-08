package com.kynsof.identity.application.query.permission.search;

import com.kynsof.identity.domain.interfaces.service.IPermissionService;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import org.springframework.stereotype.Component;

@Component
public class GetSearchPermissionQueryHandler implements IQueryHandler<GetSearchPermissionQuery, PaginatedResponse>{
    private final IPermissionService service;

    public GetSearchPermissionQueryHandler(IPermissionService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchPermissionQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
