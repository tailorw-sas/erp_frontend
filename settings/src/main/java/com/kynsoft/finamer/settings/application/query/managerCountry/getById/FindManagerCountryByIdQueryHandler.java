package com.kynsoft.finamer.settings.application.query.managerCountry.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerCountryResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerCountryDto;
import com.kynsoft.finamer.settings.domain.services.IManagerCountryService;
import org.springframework.stereotype.Component;

@Component
public class FindManagerCountryByIdQueryHandler implements IQueryHandler<FindManagerContryByIdQuery, ManagerCountryResponse>  {

    private final IManagerCountryService service;

    public FindManagerCountryByIdQueryHandler(IManagerCountryService service) {
        this.service = service;
    }

    @Override
    public ManagerCountryResponse handle(FindManagerContryByIdQuery query) {
        ManagerCountryDto response = service.findById(query.getId());

        return new ManagerCountryResponse(response);
    }
}
