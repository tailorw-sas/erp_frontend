package com.kynsoft.finamer.insis.application.query.manageB2BPartnerType.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageB2BPartnerType.ManageB2BPartnerTypeResponse;
import com.kynsoft.finamer.insis.domain.dto.ManageB2BPartnerTypeDto;
import com.kynsoft.finamer.insis.domain.services.IManageB2BPartnerTypeService;
import org.springframework.stereotype.Component;

@Component
public class FindB2BPartnerTypeByIdQueryHandler implements IQueryHandler<FindB2BPartnerTypeByIdQuery, ManageB2BPartnerTypeResponse> {

    private final IManageB2BPartnerTypeService service;

    public FindB2BPartnerTypeByIdQueryHandler(IManageB2BPartnerTypeService service){
        this.service = service;
    }

    @Override
    public ManageB2BPartnerTypeResponse handle(FindB2BPartnerTypeByIdQuery query) {
        ManageB2BPartnerTypeDto manageB2BPartnerType = service.findById(query.getId());
        return new ManageB2BPartnerTypeResponse(manageB2BPartnerType);
    }
}
