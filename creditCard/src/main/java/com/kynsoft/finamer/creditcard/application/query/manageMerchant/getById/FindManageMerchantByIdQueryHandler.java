package com.kynsoft.finamer.creditcard.application.query.manageMerchant.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageMerchantResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import org.springframework.stereotype.Component;

@Component
public class FindManageMerchantByIdQueryHandler implements IQueryHandler<FindManageMerchantByIdQuery, ManageMerchantResponse>  {

    private final IManageMerchantService service;

    public FindManageMerchantByIdQueryHandler(IManageMerchantService service) {
        this.service = service;
    }

    @Override
    public ManageMerchantResponse handle(FindManageMerchantByIdQuery query) {
        ManageMerchantDto response = service.findById(query.getId());

        return new ManageMerchantResponse(response);
    }
}
