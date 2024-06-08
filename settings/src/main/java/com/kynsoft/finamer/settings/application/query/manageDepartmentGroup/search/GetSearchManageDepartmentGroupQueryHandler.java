package com.kynsoft.finamer.settings.application.query.manageDepartmentGroup.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManageDepartmentGroupService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageDepartmentGroupQueryHandler implements IQueryHandler<GetManageDepartmentGroupQuery, PaginatedResponse>{
    private final IManageDepartmentGroupService service;

    public GetSearchManageDepartmentGroupQueryHandler(IManageDepartmentGroupService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetManageDepartmentGroupQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
