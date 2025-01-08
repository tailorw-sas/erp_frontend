package com.kynsoft.finamer.insis.application.query.innsistConnectionParamsQuery.getByTradingCompany;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.insis.application.query.objectResponse.innsistConnectionParams.InnsistConnectionParamsResponse;
import com.kynsoft.finamer.insis.domain.dto.InnsistConnectionParamsDto;
import com.kynsoft.finamer.insis.domain.services.IInnsistConnectionParamsService;
import org.springframework.stereotype.Component;

@Component
public class FindInnsistConnectionParamsByTradingCompanyQueryHandler implements IQueryHandler<FindInnsistConnectionParamsByTradingCompanyQuery, InnsistConnectionParamsResponse> {

    public final IInnsistConnectionParamsService service;

    public FindInnsistConnectionParamsByTradingCompanyQueryHandler(IInnsistConnectionParamsService service){
        this.service = service;
    }
    @Override
    public InnsistConnectionParamsResponse handle(FindInnsistConnectionParamsByTradingCompanyQuery query) {
        InnsistConnectionParamsDto connectionParamsDto = service.findByTradingCompany(query.getTradingCompanyId());

        if(connectionParamsDto != null){
            return new InnsistConnectionParamsResponse(connectionParamsDto);
        }
        return null;
    }
}
