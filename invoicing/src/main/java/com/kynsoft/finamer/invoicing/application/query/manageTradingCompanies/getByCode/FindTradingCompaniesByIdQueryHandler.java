package com.kynsoft.finamer.invoicing.application.query.manageTradingCompanies.getByCode;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageTraidingCompaniesResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageTradingCompaniesDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageTradingCompaniesService;
import org.springframework.stereotype.Component;

@Component
public class FindTradingCompaniesByIdQueryHandler implements IQueryHandler<FindTradingCompaniesByIdQuery, ManageTraidingCompaniesResponse>  {

    private final IManageTradingCompaniesService service;

    public FindTradingCompaniesByIdQueryHandler(IManageTradingCompaniesService service) {
        this.service = service;
    }

    @Override
    public ManageTraidingCompaniesResponse handle(FindTradingCompaniesByIdQuery query) {
        ManageTradingCompaniesDto response = service.findManageTradingCompaniesByCode(query.getCode());

        return new ManageTraidingCompaniesResponse(response);
    }
}
