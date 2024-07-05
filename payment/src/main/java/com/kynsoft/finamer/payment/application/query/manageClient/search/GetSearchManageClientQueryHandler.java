package com.kynsoft.finamer.payment.application.query.manageClient.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.services.IManageClientService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageClientQueryHandler implements IQueryHandler<GetSearchManageClientQuery, PaginatedResponse> {
    private final IManageClientService service;
    
    public GetSearchManageClientQueryHandler(IManageClientService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageClientQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
