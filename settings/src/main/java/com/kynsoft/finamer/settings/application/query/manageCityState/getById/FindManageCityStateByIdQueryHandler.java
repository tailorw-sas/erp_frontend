package com.kynsoft.finamer.settings.application.query.manageCityState.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageCityStateResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageCityStateDto;
import com.kynsoft.finamer.settings.domain.services.IManageCityStateService;
import org.springframework.stereotype.Component;

@Component
public class FindManageCityStateByIdQueryHandler implements IQueryHandler<FindManageCityStateByIdQuery, ManageCityStateResponse> {

    private final IManageCityStateService service;

    public FindManageCityStateByIdQueryHandler(IManageCityStateService service) {
        this.service = service;
    }

    @Override
    public ManageCityStateResponse handle(FindManageCityStateByIdQuery query) {
        ManageCityStateDto dto = service.findById(query.getId());

        return new ManageCityStateResponse(dto);
    }
}
