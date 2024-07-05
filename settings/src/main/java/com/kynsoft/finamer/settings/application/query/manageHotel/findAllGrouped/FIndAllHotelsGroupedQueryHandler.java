package com.kynsoft.finamer.settings.application.query.manageHotel.findAllGrouped;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.manageHotelGroup.ManageHotelBasicResponse;
import com.kynsoft.finamer.settings.application.query.objectResponse.manageHotelGroup.ManageHotelCountryResponse;
import com.kynsoft.finamer.settings.application.query.objectResponse.manageHotelGroup.ManageHotelGroupedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.settings.domain.services.IManageHotelService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FIndAllHotelsGroupedQueryHandler implements IQueryHandler<FindAllHotelsGroupedQuery, ManageHotelGroupedResponse> {

    private final IManageHotelService service;

    public FIndAllHotelsGroupedQueryHandler(IManageHotelService service) {
        this.service = service;
    }

    @Override
    public ManageHotelGroupedResponse handle(FindAllHotelsGroupedQuery query) {
        List<ManageHotelDto> dtoList = this.service.findAll();

        Map<String, List<ManageHotelDto>> groupedHotels = dtoList.stream()
                .collect(Collectors.groupingBy(
                        hotel -> hotel.getManageCountry().getName()
                ));
        List<ManageHotelCountryResponse> hotelCountryResponses = groupedHotels.entrySet().stream()
                .map(countryEntry -> new ManageHotelCountryResponse(
                        countryEntry.getKey(),
                        countryEntry.getValue().stream().map(
                                ManageHotelBasicResponse::new
                        ).collect(Collectors.toList())
                )).collect(Collectors.toList());
        return new ManageHotelGroupedResponse(hotelCountryResponses);
    }
}
