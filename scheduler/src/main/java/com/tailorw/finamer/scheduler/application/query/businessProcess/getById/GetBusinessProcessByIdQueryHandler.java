package com.tailorw.finamer.scheduler.application.query.businessProcess.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.tailorw.finamer.scheduler.application.query.objectResponse.BusinessProcessResponse;
import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessDto;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessService;
import org.springframework.stereotype.Component;

@Component
public class GetBusinessProcessByIdQueryHandler implements IQueryHandler<GetBusinessProcessByIdQuery, BusinessProcessResponse> {

    private final IBusinessProcessService service;

    public GetBusinessProcessByIdQueryHandler(IBusinessProcessService service){
        this.service = service;
    }

    @Override
    public BusinessProcessResponse handle(GetBusinessProcessByIdQuery query) {
        BusinessProcessDto dto = service.findById(query.getId());

        return new BusinessProcessResponse(dto);
    }
}
