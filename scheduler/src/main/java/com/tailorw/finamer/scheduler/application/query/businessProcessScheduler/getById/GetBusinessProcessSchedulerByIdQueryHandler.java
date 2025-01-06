package com.tailorw.finamer.scheduler.application.query.businessProcessScheduler.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.tailorw.finamer.scheduler.application.query.objectResponse.BusinessProcessSchedulerResponse;
import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessSchedulerDto;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessSchedulerService;
import org.springframework.stereotype.Component;

@Component
public class GetBusinessProcessSchedulerByIdQueryHandler implements IQueryHandler<GetBusinessProcessSchedulerByIdQuery, BusinessProcessSchedulerResponse> {

    private final IBusinessProcessSchedulerService service;

    public GetBusinessProcessSchedulerByIdQueryHandler(IBusinessProcessSchedulerService service){
        this.service = service;
    }

    @Override
    public BusinessProcessSchedulerResponse handle(GetBusinessProcessSchedulerByIdQuery query) {
        BusinessProcessSchedulerDto dto = service.findById(query.getId());

        return new BusinessProcessSchedulerResponse(dto);
    }
}
