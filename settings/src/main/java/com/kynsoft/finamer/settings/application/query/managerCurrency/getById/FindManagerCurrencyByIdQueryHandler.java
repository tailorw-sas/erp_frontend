package com.kynsoft.finamer.settings.application.query.managerCurrency.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerCurrencyResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerCurrencyDto;
import com.kynsoft.finamer.settings.domain.services.IManagerCurrencyService;
import org.springframework.stereotype.Component;

@Component
public class FindManagerCurrencyByIdQueryHandler implements IQueryHandler<FindManagerCurrencyByIdQuery, ManagerCurrencyResponse>  {

    private final IManagerCurrencyService service;

    public FindManagerCurrencyByIdQueryHandler(IManagerCurrencyService service) {
        this.service = service;
    }

    @Override
    public ManagerCurrencyResponse handle(FindManagerCurrencyByIdQuery query) {
        ManagerCurrencyDto response = service.findById(query.getId());

        return new ManagerCurrencyResponse(response);
    }
}
