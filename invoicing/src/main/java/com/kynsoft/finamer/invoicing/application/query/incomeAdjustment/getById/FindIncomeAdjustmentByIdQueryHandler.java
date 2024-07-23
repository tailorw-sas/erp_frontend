package com.kynsoft.finamer.invoicing.application.query.incomeAdjustment.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.IncomeAdjustmentResponse;
import com.kynsoft.finamer.invoicing.domain.dto.IncomeAdjustmentDto;
import com.kynsoft.finamer.invoicing.domain.services.IIncomeAdjustmentService;
import org.springframework.stereotype.Component;

@Component
public class FindIncomeAdjustmentByIdQueryHandler implements IQueryHandler<FindIncomeAdjustmentByIdQuery, IncomeAdjustmentResponse>  {

    private final IIncomeAdjustmentService incomeAdjustmentService;

    public FindIncomeAdjustmentByIdQueryHandler(IIncomeAdjustmentService incomeAdjustmentService) {
        this.incomeAdjustmentService = incomeAdjustmentService;
    }

    @Override
    public IncomeAdjustmentResponse handle(FindIncomeAdjustmentByIdQuery query) {
        IncomeAdjustmentDto response = incomeAdjustmentService.findById(query.getId());

        return new IncomeAdjustmentResponse(response);
    }
}
