package com.kynsof.identity.application.query.customer.getbyid;

import com.kynsof.identity.domain.dto.CustomerDto;
import com.kynsof.identity.domain.interfaces.service.ICustomerService;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import org.springframework.stereotype.Component;

@Component
public class FindCustomerByIdQueryHandler implements IQueryHandler<FindCustomerByIdQuery, CustomerResponse>  {

    private final ICustomerService service;

    public FindCustomerByIdQueryHandler(ICustomerService service) {
        this.service = service;
    }

    @Override
    public CustomerResponse handle(FindCustomerByIdQuery query) {
        CustomerDto response = service.findById(query.getId());

        return new CustomerResponse(response);
    }
}
