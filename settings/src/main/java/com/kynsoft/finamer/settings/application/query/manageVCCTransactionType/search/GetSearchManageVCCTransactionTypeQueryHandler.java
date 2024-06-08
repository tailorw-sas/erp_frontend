package com.kynsoft.finamer.settings.application.query.manageVCCTransactionType.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManageVCCTransactionTypeService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageVCCTransactionTypeQueryHandler implements IQueryHandler<GetManageVCCTransactionTypeQuery, PaginatedResponse>{
    private final IManageVCCTransactionTypeService service;

    public GetSearchManageVCCTransactionTypeQueryHandler(IManageVCCTransactionTypeService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetManageVCCTransactionTypeQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
