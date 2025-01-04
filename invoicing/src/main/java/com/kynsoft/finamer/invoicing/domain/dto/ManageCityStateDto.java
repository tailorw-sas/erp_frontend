package com.kynsoft.finamer.invoicing.domain.dto;


import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageCityStateDto {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Status status;
    private ManagerCountryDto country;
    private ManagerTimeZoneDto timeZone;

}
