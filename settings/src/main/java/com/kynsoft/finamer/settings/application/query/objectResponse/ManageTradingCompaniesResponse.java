package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageCityStateDto;
import com.kynsoft.finamer.settings.domain.dto.ManageTradingCompaniesDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerCountryDto;
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
public class ManageTradingCompaniesResponse implements IResponse {

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
    private String zipCode;
    private String innsistCode;
    private Boolean isApplyInvoice;

    public ManageTradingCompaniesResponse(ManageTradingCompaniesDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.company = dto.getCompany();
        this.cif = dto.getCif();
        this.address = dto.getAddress();
        this.country = dto.getCountry();
        this.cityState = dto.getCityState();
        this.city = dto.getCity();
        this.zipCode = dto.getZipCode();
        this.innsistCode = dto.getInnsistCode();
        this.isApplyInvoice = dto.getIsApplyInvoice();
    }
}
