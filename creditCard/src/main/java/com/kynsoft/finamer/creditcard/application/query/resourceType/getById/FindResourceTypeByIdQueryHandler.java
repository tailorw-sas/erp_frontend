package com.kynsoft.finamer.creditcard.application.query.resourceType.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.creditcard.application.query.resourceType.search.GetSearchResourceTypeResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ResourceTypeDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageResourceTypeService;
import org.springframework.stereotype.Component;

@Component
public class FindResourceTypeByIdQueryHandler implements IQueryHandler<FindResourceTypeByIdQuery, GetSearchResourceTypeResponse>  {

    private final IManageResourceTypeService service;

    public FindResourceTypeByIdQueryHandler(IManageResourceTypeService service) {
        this.service = service;
    }

    @Override
    public GetSearchResourceTypeResponse handle(FindResourceTypeByIdQuery query) {
        ResourceTypeDto response = service.findById(query.getId());

        return new GetSearchResourceTypeResponse(response);
    }
}
