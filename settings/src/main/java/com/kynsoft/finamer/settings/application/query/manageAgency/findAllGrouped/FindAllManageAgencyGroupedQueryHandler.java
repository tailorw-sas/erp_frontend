package com.kynsoft.finamer.settings.application.query.manageAgency.findAllGrouped;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.manageAgencyGroup.ManageAgencyBasicResponse;
import com.kynsoft.finamer.settings.application.query.objectResponse.manageAgencyGroup.ManageAgencyClientResponse;
import com.kynsoft.finamer.settings.application.query.objectResponse.manageAgencyGroup.ManageAgencyCountryClientResponse;
import com.kynsoft.finamer.settings.application.query.objectResponse.manageAgencyGroup.ManageAgencyGroupedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.settings.domain.services.IManageAgencyService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FindAllManageAgencyGroupedQueryHandler implements IQueryHandler<FindAllManageAgencyGroupedQuery, ManageAgencyGroupedResponse> {

    private final IManageAgencyService service;

    public FindAllManageAgencyGroupedQueryHandler(IManageAgencyService service) {
        this.service = service;
    }

    @Override
    public ManageAgencyGroupedResponse handle(FindAllManageAgencyGroupedQuery query) {
        List<ManageAgencyDto> dtoList = this.service.findAll();

        Map<String, Map<String, List<ManageAgencyDto>>> groupedAgencies = dtoList.stream()
                .collect(Collectors.groupingBy(
                        agency -> agency.getCountry().getName(),
                        Collectors.groupingBy(
                                agency -> agency.getClient().getName()
                        )
                ));

        List<ManageAgencyCountryClientResponse> countryClientAgencies = groupedAgencies.entrySet().stream()
                .map(countryEntry -> new ManageAgencyCountryClientResponse(
                        countryEntry.getKey(),
                        countryEntry.getValue().entrySet().stream()
                                .map(clientEntry -> new ManageAgencyClientResponse(
                                        clientEntry.getKey(),
                                        clientEntry.getValue().stream().map(
                                                ManageAgencyBasicResponse::new
                                        ).collect(Collectors.toList())
                                ))
                                .collect(Collectors.toList())
                )).collect(Collectors.toList());

        return new ManageAgencyGroupedResponse(countryClientAgencies);
    }
}
