package com.kynsoft.finamer.settings.application.query.manageEmployee.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManageEmployeeService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageEmployeeQueryHandler implements IQueryHandler<GetManageEmployeeQuery, PaginatedResponse>{
    private final IManageEmployeeService service;

    public GetSearchManageEmployeeQueryHandler(IManageEmployeeService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetManageEmployeeQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
