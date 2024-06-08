package com.kynsoft.finamer.settings.application.query.managerMerchantCurrency.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerMerchantCurrencyResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantCurrencyDto;
import com.kynsoft.finamer.settings.domain.services.IManagerMerchantCurrencyService;
import org.springframework.stereotype.Component;

@Component
public class FindManagerMerchantCurrencyByIdQueryHandler implements IQueryHandler<FindManagerMerchantCurrencyByIdQuery, ManagerMerchantCurrencyResponse>  {

    private final IManagerMerchantCurrencyService service;

    public FindManagerMerchantCurrencyByIdQueryHandler(IManagerMerchantCurrencyService service) {
        this.service = service;
    }

    @Override
    public ManagerMerchantCurrencyResponse handle(FindManagerMerchantCurrencyByIdQuery query) {
        ManagerMerchantCurrencyDto response = service.findById(query.getId());

        return new ManagerMerchantCurrencyResponse(response);
    }
}
