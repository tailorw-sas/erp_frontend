package com.kynsoft.finamer.invoicing.application.query.incomeAdjustment.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IIncomeAdjustmentService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchIncomeAdjustmentQueryHandler implements IQueryHandler<GetSearchIncomeAdjustmentQuery, PaginatedResponse> {
    private final IIncomeAdjustmentService incomeAdjustmentService;
    
    public GetSearchIncomeAdjustmentQueryHandler(IIncomeAdjustmentService incomeAdjustmentService) {
        this.incomeAdjustmentService = incomeAdjustmentService;
    }

    @Override
    public PaginatedResponse handle(GetSearchIncomeAdjustmentQuery query) {

        return this.incomeAdjustmentService.search(query.getPageable(),query.getFilter());
    }
}
