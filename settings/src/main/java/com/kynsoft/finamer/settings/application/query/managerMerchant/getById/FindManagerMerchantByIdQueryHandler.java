package com.kynsoft.finamer.settings.application.query.managerMerchant.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerMerchantResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantDto;
import com.kynsoft.finamer.settings.domain.services.IManagerMerchantService;
import org.springframework.stereotype.Component;

@Component
public class FindManagerMerchantByIdQueryHandler implements IQueryHandler<FindManagerMerchantByIdQuery, ManagerMerchantResponse>  {

    private final IManagerMerchantService service;

    public FindManagerMerchantByIdQueryHandler(IManagerMerchantService service) {
        this.service = service;
    }

    @Override
    public ManagerMerchantResponse handle(FindManagerMerchantByIdQuery query) {
        ManagerMerchantDto response = service.findById(query.getId());

        return new ManagerMerchantResponse(response);
    }
}
