package com.kynsoft.finamer.settings.application.query.manageModule.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManageInvoiceTypeService;
import com.kynsoft.finamer.settings.domain.services.IManageModuleService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageModuleHandler implements IQueryHandler<GetSearchManageModuleQuery, PaginatedResponse> {

    private final IManageModuleService service;

    public GetSearchManageModuleHandler(IManageModuleService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageModuleQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
