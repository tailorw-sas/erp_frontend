package com.kynsoft.finamer.settings.domain.dto;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageTradingCompaniesDto {

    private UUID id;
    private String code;
    private String description;
    private Status status;
    private String company;
    private String cif;
    private String address;
    private ManagerCountryDto country;
    private ManageCityStateDto cityState;
    private String city;
    private Long zipCode;
    private String innsistCode;
    private Boolean isApplyInvoice;
}
