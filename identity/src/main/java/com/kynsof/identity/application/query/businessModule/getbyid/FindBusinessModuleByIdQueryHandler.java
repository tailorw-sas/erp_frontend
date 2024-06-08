package com.kynsof.identity.application.query.businessModule.getbyid;

import com.kynsof.identity.application.query.businessModule.search.BusinessModuleResponse;
import com.kynsof.identity.domain.dto.BusinessModuleDto;
import com.kynsof.identity.domain.interfaces.service.IBusinessModuleService;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import org.springframework.stereotype.Component;

@Component
public class FindBusinessModuleByIdQueryHandler implements IQueryHandler<FindBusinessModuleByIdQuery, BusinessModuleResponse>  {

    private final IBusinessModuleService service;

    public FindBusinessModuleByIdQueryHandler(IBusinessModuleService service) {
        this.service = service;
    }

    @Override
    public BusinessModuleResponse handle(FindBusinessModuleByIdQuery query) {
        BusinessModuleDto response = service.findById(query.getId());

        return new BusinessModuleResponse(response);
    }
}
