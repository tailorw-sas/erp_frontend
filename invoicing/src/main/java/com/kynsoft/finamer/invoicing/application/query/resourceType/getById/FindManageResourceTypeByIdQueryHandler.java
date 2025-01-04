package com.kynsoft.finamer.invoicing.application.query.resourceType.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ResourceTypeResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ResourceTypeDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageResourceTypeService;
import org.springframework.stereotype.Component;

@Component
public class FindManageResourceTypeByIdQueryHandler implements IQueryHandler<FindManageResourceTypeByIdQuery, ResourceTypeResponse>  {

    private final IManageResourceTypeService service;

    public FindManageResourceTypeByIdQueryHandler(IManageResourceTypeService service) {
        this.service = service;
    }

    @Override
    public ResourceTypeResponse handle(FindManageResourceTypeByIdQuery query) {
        ResourceTypeDto response = service.findById(query.getId());

        return new ResourceTypeResponse(response);
    }
}
