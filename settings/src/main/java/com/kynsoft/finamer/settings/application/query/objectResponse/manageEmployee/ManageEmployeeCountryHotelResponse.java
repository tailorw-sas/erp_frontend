package com.kynsoft.finamer.settings.application.query.objectResponse.manageEmployee;

import com.kynsoft.finamer.settings.application.query.objectResponse.manageHotelGroup.ManageHotelBasicResponse;
import com.kynsoft.finamer.settings.domain.dto.manageEmployeeGroup.ManageEmployeeCountryHotelDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ManageEmployeeCountryHotelResponse {

    private String countryName;
    private List<ManageHotelBasicResponse> hotels;

    public ManageEmployeeCountryHotelResponse(ManageEmployeeCountryHotelDto dto){
        this.countryName = dto.getCountryName();
        this.hotels = dto.getHotels() != null ? dto.getHotels().stream().map(ManageHotelBasicResponse :: new).toList() : null;
    }
}
