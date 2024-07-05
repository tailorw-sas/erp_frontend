package com.kynsoft.finamer.settings.domain.dto.manageEmployeeGroup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageEmployeeCountryClientAgencyDto {
    private String countryName;
    private List<ManageEmployeeClientAgencyDto> clients;
}
