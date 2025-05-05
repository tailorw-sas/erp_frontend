package com.kynsoft.finamer.settings.infrastructure.projections;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManageTradingCompanyProjection {

    private UUID id;
    private String code;
    private Status status;
    private String description;
    private String company;
    private String cif;
    private String address;
    private ManageCountryProjection country;
    private ManageCityStateProjection cityState;
    private String city;
    private String zipCode;
    private String innsistCode;
    private Boolean isApplyInvoice;

    public ManageTradingCompanyProjection(UUID id,
                                          String code,
                                          Status status,
                                          String company){
        this.id = id;
        this.code = code;
        this.status = status;
        this.company = company;
    }
}
