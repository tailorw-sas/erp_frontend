package com.kynsoft.finamer.settings.application.query.manageB2BPartnerType.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageB2BPartnerTypeResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageB2BPartnerTypeDto;
import com.kynsoft.finamer.settings.domain.services.IManageB2BPartnerTypeService;
import org.springframework.stereotype.Component;

@Component
public class FindManagerB2BPartnerTypeByIdQueryHandler implements IQueryHandler<FindManagerB2BPartnerTypeByIdQuery, ManageB2BPartnerTypeResponse>  {

    private final IManageB2BPartnerTypeService service;

    public FindManagerB2BPartnerTypeByIdQueryHandler(IManageB2BPartnerTypeService service) {
        this.service = service;
    }

    @Override
    public ManageB2BPartnerTypeResponse handle(FindManagerB2BPartnerTypeByIdQuery query) {
        ManageB2BPartnerTypeDto response = service.findById(query.getId());

        return new ManageB2BPartnerTypeResponse(response);
    }
}
