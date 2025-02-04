package com.kynsoft.finamer.insis.application.query.manageAgency.getIdsByCodes;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageAgency.ManageAgencyIdsResponse;
import com.kynsoft.finamer.insis.domain.services.IManageAgencyService;
import org.springframework.stereotype.Component;

@Component
public class GetManageAgencyIdsByCodesQueryHandler implements IQueryHandler<GetManageAgencyIdsByCodesQuery, ManageAgencyIdsResponse> {

    private final IManageAgencyService service;

    public GetManageAgencyIdsByCodesQueryHandler(IManageAgencyService service){
        this.service = service;
    }

    @Override
    public ManageAgencyIdsResponse handle(GetManageAgencyIdsByCodesQuery query) {
        return new ManageAgencyIdsResponse(service.findIdsByCodes(query.getCodes()));
    }
}
