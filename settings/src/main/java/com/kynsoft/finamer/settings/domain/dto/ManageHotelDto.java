package com.kynsoft.finamer.settings.domain.dto;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ManageHotelDto {

    private UUID id;
    private String code;
    private String description;
    private Status status;
    private String name;
    private String babelCode;
    private ManagerCountryDto manageCountry;
    private ManageCityStateDto manageCityState;
    private String city;
    private String address;
    private ManagerCurrencyDto manageCurrency;
    private ManageRegionDto manageRegion;
    private ManageTradingCompaniesDto manageTradingCompanies;
    private Boolean applyByTradingCompany;
    private String prefixToInvoice;
    private Boolean isVirtual;
    private Boolean requiresFlatRate;
    private Boolean isApplyByVCC;
    private Boolean autoApplyCredit;
}
