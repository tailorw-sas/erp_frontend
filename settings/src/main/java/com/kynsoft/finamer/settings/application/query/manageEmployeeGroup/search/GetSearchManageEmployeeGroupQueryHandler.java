package com.kynsoft.finamer.settings.application.query.manageEmployeeGroup.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManageEmployeeGroupService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageEmployeeGroupQueryHandler implements IQueryHandler<GetManageEmployeeGroupQuery, PaginatedResponse>{
    private final IManageEmployeeGroupService service;

    public GetSearchManageEmployeeGroupQueryHandler(IManageEmployeeGroupService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetManageEmployeeGroupQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
