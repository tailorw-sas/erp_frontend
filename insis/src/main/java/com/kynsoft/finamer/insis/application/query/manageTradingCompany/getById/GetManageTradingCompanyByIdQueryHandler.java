package com.kynsoft.finamer.insis.application.query.manageTradingCompany.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageTradingCompany.ManageTradingCompanyResponse;
import com.kynsoft.finamer.insis.domain.services.IManageTradingCompanyService;
import org.springframework.stereotype.Component;

@Component
public class GetManageTradingCompanyByIdQueryHandler implements IQueryHandler<GetManageTradingCompanyByIdQuery, ManageTradingCompanyResponse> {

    private final IManageTradingCompanyService service;

    public GetManageTradingCompanyByIdQueryHandler(IManageTradingCompanyService service){
        this.service = service;
    }

    @Override
    public ManageTradingCompanyResponse handle(GetManageTradingCompanyByIdQuery query) {
        return new ManageTradingCompanyResponse(service.findById(query.getId()));
    }
}
