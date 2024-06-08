package com.kynsoft.finamer.settings.application.query.managerCreditCardType.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageCreditCardTypeResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageCreditCardTypeDto;
import com.kynsoft.finamer.settings.domain.services.IManageCreditCardTypeService;
import org.springframework.stereotype.Component;

@Component
public class FindManageCreditCardTypeByIdQueryHandler implements IQueryHandler<FindManageCreditCardTypeByIdQuery, ManageCreditCardTypeResponse>  {

    private final IManageCreditCardTypeService service;

    public FindManageCreditCardTypeByIdQueryHandler(IManageCreditCardTypeService service) {
        this.service = service;
    }

    @Override
    public ManageCreditCardTypeResponse handle(FindManageCreditCardTypeByIdQuery query) {
        ManageCreditCardTypeDto response = service.findById(query.getId());

        return new ManageCreditCardTypeResponse(response);
    }
}
