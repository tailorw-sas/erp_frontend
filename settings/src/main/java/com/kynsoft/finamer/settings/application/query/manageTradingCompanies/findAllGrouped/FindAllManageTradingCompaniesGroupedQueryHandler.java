package com.kynsoft.finamer.settings.application.query.manageTradingCompanies.findAllGrouped;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.manageTradingCompanies.ManageTradingCompaniesBasicResponse;
import com.kynsoft.finamer.settings.application.query.objectResponse.manageTradingCompanies.ManageTradingCompaniesCountryResponse;
import com.kynsoft.finamer.settings.application.query.objectResponse.manageTradingCompanies.ManageTradingCompaniesGroupedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageTradingCompaniesDto;
import com.kynsoft.finamer.settings.domain.services.IManageTradingCompaniesService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FindAllManageTradingCompaniesGroupedQueryHandler implements IQueryHandler<FindAllManageTradingCompaniesGroupedQuery, ManageTradingCompaniesGroupedResponse> {

    private final IManageTradingCompaniesService service;

    public FindAllManageTradingCompaniesGroupedQueryHandler(IManageTradingCompaniesService service) {
        this.service = service;
    }

    @Override
    public ManageTradingCompaniesGroupedResponse handle(FindAllManageTradingCompaniesGroupedQuery query) {
        List<ManageTradingCompaniesDto> dtoList = this.service.findAll();

        Map<String, List<ManageTradingCompaniesDto>> groupedInstances = dtoList.stream()
                .collect(Collectors.groupingBy(
                        instance -> instance.getCountry().getName()
                ));

        List<ManageTradingCompaniesCountryResponse> countryResponses = groupedInstances.entrySet().stream()
                .map(countryEntry -> new ManageTradingCompaniesCountryResponse(
                        countryEntry.getKey(),
                        countryEntry.getValue().stream().map(
                                ManageTradingCompaniesBasicResponse::new
                        ).collect(Collectors.toList())
                )).collect(Collectors.toList());

        return new ManageTradingCompaniesGroupedResponse(countryResponses);
    }
}
