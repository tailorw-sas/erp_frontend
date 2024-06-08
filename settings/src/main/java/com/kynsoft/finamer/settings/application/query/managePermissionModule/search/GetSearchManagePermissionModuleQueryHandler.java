package com.kynsoft.finamer.settings.application.query.managePermissionModule.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManagePermissionModuleService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManagePermissionModuleQueryHandler implements IQueryHandler<GetManagePermissionModuleQuery, PaginatedResponse>{
    private final IManagePermissionModuleService service;

    public GetSearchManagePermissionModuleQueryHandler(IManagePermissionModuleService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetManagePermissionModuleQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
