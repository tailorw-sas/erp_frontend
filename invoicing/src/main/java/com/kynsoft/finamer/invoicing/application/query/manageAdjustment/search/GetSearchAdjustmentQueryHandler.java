package com.kynsoft.finamer.invoicing.application.query.manageAdjustment.search;


import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IManageAdjustmentService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchAdjustmentQueryHandler implements IQueryHandler<GetSearchAdjustmentQuery, PaginatedResponse> {
    private final IManageAdjustmentService service;
    
    public GetSearchAdjustmentQueryHandler(IManageAdjustmentService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchAdjustmentQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
