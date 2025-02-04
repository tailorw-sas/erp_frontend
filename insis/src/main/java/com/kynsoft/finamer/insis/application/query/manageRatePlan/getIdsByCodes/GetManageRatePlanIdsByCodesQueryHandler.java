package com.kynsoft.finamer.insis.application.query.manageRatePlan.getIdsByCodes;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageRatePlan.ManageRatePlanIdsResponse;
import com.kynsoft.finamer.insis.domain.services.IManageRatePlanService;
import org.springframework.stereotype.Component;

@Component
public class GetManageRatePlanIdsByCodesQueryHandler implements IQueryHandler<GetManageRatePlanIdsByCodesQuery, ManageRatePlanIdsResponse> {

    private final IManageRatePlanService service;

    public GetManageRatePlanIdsByCodesQueryHandler(IManageRatePlanService service){
        this.service = service;
    }

    @Override
    public ManageRatePlanIdsResponse handle(GetManageRatePlanIdsByCodesQuery query) {
        return new ManageRatePlanIdsResponse(service.findIdsByCodes(query.getHotel(), query.getCodes()));
    }
}
