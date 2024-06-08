package com.kynsoft.finamer.settings.application.query.manageTradingCompanies.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageTradingCompaniesResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageTradingCompaniesDto;
import com.kynsoft.finamer.settings.domain.services.IManageTradingCompaniesService;
import org.springframework.stereotype.Component;

@Component
public class FindManageTradingCompaniesByIdQueryHandler implements IQueryHandler<FindManageTradingCompaniesByIdQuery, ManageTradingCompaniesResponse> {

    private final IManageTradingCompaniesService service;

    public FindManageTradingCompaniesByIdQueryHandler(IManageTradingCompaniesService service) {
        this.service = service;
    }

    @Override
    public ManageTradingCompaniesResponse handle(FindManageTradingCompaniesByIdQuery query) {
        ManageTradingCompaniesDto dto = service.findById(query.getId());
        return new ManageTradingCompaniesResponse(dto);
    }
}
