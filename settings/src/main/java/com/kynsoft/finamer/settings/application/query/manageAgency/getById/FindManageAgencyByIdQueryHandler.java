package com.kynsoft.finamer.settings.application.query.manageAgency.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageAgencyResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.settings.domain.services.IManageAgencyService;
import org.springframework.stereotype.Component;

@Component
public class FindManageAgencyByIdQueryHandler implements IQueryHandler<FindManageAgencyByIdQuery, ManageAgencyResponse> {

    private final IManageAgencyService service;

    public FindManageAgencyByIdQueryHandler(IManageAgencyService service) {
        this.service = service;
    }

    @Override
    public ManageAgencyResponse handle(FindManageAgencyByIdQuery query) {
        ManageAgencyDto dto = service.findById(query.getId());
        return new ManageAgencyResponse(dto);
    }
}
