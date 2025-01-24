package com.kynsoft.finamer.insis.application.query.innsistConnectionParamsQuery.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.insis.application.query.objectResponse.innsistConnectionParams.InnsistConnectionParamsResponse;
import com.kynsoft.finamer.insis.domain.dto.InnsistConnectionParamsDto;
import com.kynsoft.finamer.insis.domain.services.IInnsistConnectionParamsService;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class FindInnsistConnectionParamsByIdQueryHandler implements IQueryHandler<FindInnsistConnectionParamsByIdQuery, InnsistConnectionParamsResponse> {
    private final IInnsistConnectionParamsService service;

    public FindInnsistConnectionParamsByIdQueryHandler(IInnsistConnectionParamsService service){
        this.service = service;
    }

    @Override
    public InnsistConnectionParamsResponse handle(FindInnsistConnectionParamsByIdQuery query) {
        InnsistConnectionParamsDto dto = service.findById(query.getId());
        if(Objects.nonNull(dto)){
            return new InnsistConnectionParamsResponse(dto);
        }else {
            return null;
        }

    }
}
