package com.kynsoft.finamer.insis.application.query.innsistConnectionParamsQuery.hasTradingCompanyAssociated;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.insis.application.query.objectResponse.innsistConnectionParams.HasTradingCompanyAssociatedResponse;
import com.kynsoft.finamer.insis.domain.services.IInnsistConnectionParamsService;
import org.springframework.stereotype.Component;

@Component
public class CheckTradingCompanyAssociatedQueryHandler implements IQueryHandler<CheckTradingCompanyAssociatedQuery, HasTradingCompanyAssociatedResponse> {

    private final IInnsistConnectionParamsService service;

    public CheckTradingCompanyAssociatedQueryHandler(IInnsistConnectionParamsService service){
        this.service = service;
    }

    @Override
    public HasTradingCompanyAssociatedResponse handle(CheckTradingCompanyAssociatedQuery query) {
        return new HasTradingCompanyAssociatedResponse(service.hasTradingCompanyAssociation(query.getId()));
    }
}
