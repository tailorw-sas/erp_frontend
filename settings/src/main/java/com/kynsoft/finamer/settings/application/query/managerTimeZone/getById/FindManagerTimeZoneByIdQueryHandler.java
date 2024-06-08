package com.kynsoft.finamer.settings.application.query.managerTimeZone.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerTimeZoneResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerTimeZoneDto;
import com.kynsoft.finamer.settings.domain.services.IManagerTimeZoneService;
import org.springframework.stereotype.Component;

@Component
public class FindManagerTimeZoneByIdQueryHandler implements IQueryHandler<FindManagerTimeZoneByIdQuery, ManagerTimeZoneResponse> {

    private final IManagerTimeZoneService service;

    public FindManagerTimeZoneByIdQueryHandler(IManagerTimeZoneService service) {
        this.service = service;
    }

    @Override
    public ManagerTimeZoneResponse handle(FindManagerTimeZoneByIdQuery query) {
        ManagerTimeZoneDto dto = service.findById(query.getId());

        return new ManagerTimeZoneResponse(dto);
    }
}
