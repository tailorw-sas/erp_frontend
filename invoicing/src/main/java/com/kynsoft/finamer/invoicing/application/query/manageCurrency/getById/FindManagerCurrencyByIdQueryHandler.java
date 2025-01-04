package com.kynsoft.finamer.invoicing.application.query.manageCurrency.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageCurrencyResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageCurrencyDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageCurrencyService;
import org.springframework.stereotype.Component;

@Component
public class FindManagerCurrencyByIdQueryHandler implements IQueryHandler<FindManagerCurrencyByIdQuery, ManageCurrencyResponse>  {

    private final IManageCurrencyService service;

    public FindManagerCurrencyByIdQueryHandler(IManageCurrencyService service) {
        this.service = service;
    }

    @Override
    public ManageCurrencyResponse handle(FindManagerCurrencyByIdQuery query) {
        ManageCurrencyDto response = service.findById(query.getId());

        return new ManageCurrencyResponse(response);
    }
}
