package com.kynsoft.finamer.invoicing.application.query.manageRatePlan.byCodeAndHotelCode;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageRatePlanResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRatePlanDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageRatePlanService;
import org.springframework.stereotype.Component;

@Component
public class FindManageRatePlanByCodeAndHotelCodeQueryHandler implements IQueryHandler<FindManageRatePlanByCodeAndHotelCodeQuery, ManageRatePlanResponse> {

    private final IManageRatePlanService ratePlanService;

    public FindManageRatePlanByCodeAndHotelCodeQueryHandler(IManageRatePlanService ratePlanService) {
        this.ratePlanService = ratePlanService;
    }

    @Override
    public ManageRatePlanResponse handle(FindManageRatePlanByCodeAndHotelCodeQuery query) {
        ManageRatePlanDto dto = this.ratePlanService.findManageRatePlanByCodeAndHotelCode(query.getCode(), query.getHotelCode());

        return new ManageRatePlanResponse(dto);
    }
}
