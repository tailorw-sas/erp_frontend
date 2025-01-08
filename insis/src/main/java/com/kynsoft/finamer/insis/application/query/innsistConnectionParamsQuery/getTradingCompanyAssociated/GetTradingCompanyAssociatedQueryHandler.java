package com.kynsoft.finamer.insis.application.query.innsistConnectionParamsQuery.getTradingCompanyAssociated;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageTradingCompany.ManageTradingCompanyResponse;
import com.kynsoft.finamer.insis.domain.dto.ManageTradingCompanyDto;
import com.kynsoft.finamer.insis.domain.services.IInnsistConnectionParamsService;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class GetTradingCompanyAssociatedQueryHandler implements IQueryHandler<GetTradingCompanyAssociatedQuery, ManageTradingCompanyResponse> {

    private final IInnsistConnectionParamsService service;

    public GetTradingCompanyAssociatedQueryHandler(IInnsistConnectionParamsService service){
        this.service = service;
    }

    @Override
    public ManageTradingCompanyResponse handle(GetTradingCompanyAssociatedQuery query) {
        ManageTradingCompanyDto dto = service.findTradingCompanyAssociated(query.getId());
        if(Objects.nonNull(dto)){
            return new ManageTradingCompanyResponse(dto);
        }
        return new ManageTradingCompanyResponse();
    }
}
