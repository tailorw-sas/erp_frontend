package com.kynsoft.finamer.payment.application.query.manageResourceType.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.payment.application.query.objectResponse.ManageResourceTypeResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageResourceTypeDto;
import com.kynsoft.finamer.payment.domain.services.IManageResourceTypeService;
import org.springframework.stereotype.Component;

@Component
public class FindManageResourceTypeByIdQueryHandler implements IQueryHandler<FindManageResourceTypeByIdQuery, ManageResourceTypeResponse>  {

    private final IManageResourceTypeService service;

    public FindManageResourceTypeByIdQueryHandler(IManageResourceTypeService service) {
        this.service = service;
    }

    @Override
    public ManageResourceTypeResponse handle(FindManageResourceTypeByIdQuery query) {
        ManageResourceTypeDto response = service.findById(query.getId());

        return new ManageResourceTypeResponse(response);
    }
}
