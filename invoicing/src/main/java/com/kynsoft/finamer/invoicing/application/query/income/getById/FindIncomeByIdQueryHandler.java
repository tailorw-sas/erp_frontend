package com.kynsoft.finamer.invoicing.application.query.income.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.IncomeResponse;
import com.kynsoft.finamer.invoicing.domain.dto.IncomeDto;
import com.kynsoft.finamer.invoicing.domain.services.IIncomeService;
import org.springframework.stereotype.Component;

@Component
public class FindIncomeByIdQueryHandler implements IQueryHandler<FindIncomeByIdQuery, IncomeResponse>  {

    private final IIncomeService service;

    public FindIncomeByIdQueryHandler(IIncomeService service) {
        this.service = service;
    }

    @Override
    public IncomeResponse handle(FindIncomeByIdQuery query) {
        IncomeDto response = service.findById(query.getId());

        return new IncomeResponse(response);
    }
}
