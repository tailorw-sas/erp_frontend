package com.kynsoft.finamer.settings.application.query.objectResponse.manageTradingCompanies;

import com.kynsoft.finamer.settings.domain.dto.ManageTradingCompaniesDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.infrastructure.projections.ManageTradingCompanyProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageTradingCompaniesBasicResponse {

    private UUID id;
    private String code;
    private Status status;
    private String company;

    public ManageTradingCompaniesBasicResponse(ManageTradingCompaniesDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
        this.company = dto.getCompany();
    }

    public ManageTradingCompaniesBasicResponse(ManageTradingCompanyProjection projection){
        this.id = projection.getId();
        this.code = projection.getCode();
        this.status = projection.getStatus();
        this.company = projection.getCompany();
    }
}
