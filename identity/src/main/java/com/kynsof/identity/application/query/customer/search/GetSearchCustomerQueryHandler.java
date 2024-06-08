package com.kynsof.identity.application.query.customer.search;

import com.kynsof.identity.domain.interfaces.service.ICustomerService;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import org.springframework.stereotype.Component;

@Component
public class GetSearchCustomerQueryHandler implements IQueryHandler<GetSearchCustomerQuery, PaginatedResponse>{
    private final ICustomerService service;
    
    public GetSearchCustomerQueryHandler(ICustomerService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchCustomerQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
