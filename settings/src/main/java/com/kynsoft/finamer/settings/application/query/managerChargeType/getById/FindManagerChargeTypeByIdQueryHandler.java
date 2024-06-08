package com.kynsoft.finamer.settings.application.query.managerChargeType.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerChargeTypeResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerChargeTypeDto;
import com.kynsoft.finamer.settings.domain.services.IManagerChargeTypeService;
import org.springframework.stereotype.Component;

@Component
public class FindManagerChargeTypeByIdQueryHandler implements IQueryHandler<FindManagerChargeTypeByIdQuery, ManagerChargeTypeResponse>  {

    private final IManagerChargeTypeService service;

    public FindManagerChargeTypeByIdQueryHandler(IManagerChargeTypeService service) {
        this.service = service;
    }

    @Override
    public ManagerChargeTypeResponse handle(FindManagerChargeTypeByIdQuery query) {
        ManagerChargeTypeDto response = service.findById(query.getId());

        return new ManagerChargeTypeResponse(response);
    }
}
