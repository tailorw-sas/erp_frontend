package com.kynsoft.finamer.settings.application.query.manageRatePlan.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageRatePlanResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageRatePlanDto;
import com.kynsoft.finamer.settings.domain.services.IManageRatePlanService;
import org.springframework.stereotype.Component;

@Component
public class FindManageRateClientByIdQueryHandler implements IQueryHandler<FindManageRateClientByIdQuery, ManageRatePlanResponse> {

    private final IManageRatePlanService service;

    public FindManageRateClientByIdQueryHandler(IManageRatePlanService service) {
        this.service = service;
    }

    @Override
    public ManageRatePlanResponse handle(FindManageRateClientByIdQuery query) {
        ManageRatePlanDto dto = service.findById(query.getId());

        return new ManageRatePlanResponse(dto);
    }
}
