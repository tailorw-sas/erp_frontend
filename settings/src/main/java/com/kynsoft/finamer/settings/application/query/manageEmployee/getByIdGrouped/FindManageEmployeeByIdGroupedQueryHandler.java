package com.kynsoft.finamer.settings.application.query.manageEmployee.getByIdGrouped;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.manageEmployee.ManageEmployeeCountryClientAgencyResponse;
import com.kynsoft.finamer.settings.application.query.objectResponse.manageEmployee.ManageEmployeeCountryHotelResponse;
import com.kynsoft.finamer.settings.application.query.objectResponse.manageEmployee.ManageEmployeeGroupedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.settings.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.settings.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.settings.domain.dto.manageEmployeeGroup.ManageEmployeeClientAgencyDto;
import com.kynsoft.finamer.settings.domain.dto.manageEmployeeGroup.ManageEmployeeCountryClientAgencyDto;
import com.kynsoft.finamer.settings.domain.dto.manageEmployeeGroup.ManageEmployeeCountryHotelDto;
import com.kynsoft.finamer.settings.domain.services.IManageEmployeeService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FindManageEmployeeByIdGroupedQueryHandler implements IQueryHandler<FindManageEmployeeByIdGroupedQuery, ManageEmployeeGroupedResponse> {

    private final IManageEmployeeService service;

    public FindManageEmployeeByIdGroupedQueryHandler(IManageEmployeeService service) {
        this.service = service;
    }

    @Override
    public ManageEmployeeGroupedResponse handle(FindManageEmployeeByIdGroupedQuery query) {
        ManageEmployeeDto dto = this.service.findById(query.getId());

        //Agencias
        // Agrupar agencias por país y cliente
        Map<String, Map<String, List<ManageAgencyDto>>> groupedAgencies = dto.getManageAgencyList().stream()
                .collect(Collectors.groupingBy(
                        agency -> agency.getCountry().getName(),
                        Collectors.groupingBy(
                                agency -> agency.getClient().getName()
                        )
                ));

        // Construir la estructura de respuesta
        List<ManageEmployeeCountryClientAgencyDto> countryClientAgencyDtos = groupedAgencies.entrySet().stream()
                .map(countryEntry -> new ManageEmployeeCountryClientAgencyDto(
                        countryEntry.getKey(),
                        countryEntry.getValue().entrySet().stream()
                                .map(clientEntry -> new ManageEmployeeClientAgencyDto(
                                        clientEntry.getKey(),
                                        clientEntry.getValue()
                                ))
                                .collect(Collectors.toList())
                ))
                .toList();

        List<ManageEmployeeCountryClientAgencyResponse> agencies = countryClientAgencyDtos.stream().map(
                ManageEmployeeCountryClientAgencyResponse::new
        ).toList();

        //Hoteles
        //Agrupar hoteles por países
        Map<String, List<ManageHotelDto>> groupedHotels = dto.getManageHotelList().stream()
                .collect(Collectors.groupingBy(
                        hotel -> hotel.getManageCountry().getName()
                ));

        //Construir estructura de la respuesta
        List<ManageEmployeeCountryHotelDto> countryHotelDtos = groupedHotels.entrySet().stream()
                .map(country -> new ManageEmployeeCountryHotelDto(
                        country.getKey(),
                        country.getValue()
                )).toList();

        List<ManageEmployeeCountryHotelResponse> hotels = countryHotelDtos.stream().map(
                ManageEmployeeCountryHotelResponse :: new
        ).toList();

        return new ManageEmployeeGroupedResponse(query.getId(), agencies, hotels);
    }
}
