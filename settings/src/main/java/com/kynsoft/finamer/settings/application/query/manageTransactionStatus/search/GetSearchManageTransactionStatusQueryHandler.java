package com.kynsoft.finamer.settings.application.query.manageTransactionStatus.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManageTransactionStatusService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageTransactionStatusQueryHandler implements IQueryHandler<GetManageTransactionStatusQuery, PaginatedResponse>{
    private final IManageTransactionStatusService service;

    public GetSearchManageTransactionStatusQueryHandler(IManageTransactionStatusService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetManageTransactionStatusQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
