package com.kynsoft.finamer.creditcard.application.query.managerAccountType.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManagerAccountTypeResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerAccountTypeDto;
import com.kynsoft.finamer.creditcard.domain.services.IManagerAccountTypeService;
import org.springframework.stereotype.Component;

@Component
public class FindManagerAccountTypeByIdQueryHandler implements IQueryHandler<FindManagerAccountTypeByIdQuery, ManagerAccountTypeResponse>  {

    private final IManagerAccountTypeService service;

    public FindManagerAccountTypeByIdQueryHandler(IManagerAccountTypeService service) {
        this.service = service;
    }

    @Override
    public ManagerAccountTypeResponse handle(FindManagerAccountTypeByIdQuery query) {
        ManagerAccountTypeDto response = service.findById(query.getId());

        return new ManagerAccountTypeResponse(response);
    }
}
