package com.kynsoft.finamer.settings.application.query.manageRegion.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageRegionResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageRegionDto;
import com.kynsoft.finamer.settings.domain.services.IManageRegionService;
import org.springframework.stereotype.Component;

@Component
public class FindManageRegionByIdQueryHandler implements IQueryHandler<FindManageRegionByIdQuery, ManageRegionResponse> {

    private final IManageRegionService service;

    public FindManageRegionByIdQueryHandler(IManageRegionService service) {
        this.service = service;
    }

    @Override
    public ManageRegionResponse handle(FindManageRegionByIdQuery query) {
        ManageRegionDto dto = service.findById(query.getId());

        return new ManageRegionResponse(dto);
    }
}
