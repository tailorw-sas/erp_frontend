package com.kynsoft.finamer.settings.application.query.managerLanguage.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerLanguageResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerLanguageDto;
import com.kynsoft.finamer.settings.domain.services.IManagerLanguageService;
import org.springframework.stereotype.Component;

@Component
public class FindManagerLanguageByIdQueryHandler implements IQueryHandler<FindManagerLanguageByIdQuery, ManagerLanguageResponse> {

    private final IManagerLanguageService service;

    public FindManagerLanguageByIdQueryHandler(IManagerLanguageService service) {
        this.service = service;
    }

    @Override
    public ManagerLanguageResponse handle(FindManagerLanguageByIdQuery query) {
        ManagerLanguageDto dto = service.findById(query.getId());
        return new ManagerLanguageResponse(dto);
    }
}
