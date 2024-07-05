package com.kynsoft.finamer.settings.domain.dto.manageEmployeeGroup;

import com.kynsoft.finamer.settings.domain.dto.ManageHotelDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageEmployeeCountryHotelDto {

    private String countryName;
    private List<ManageHotelDto> hotels;
}
