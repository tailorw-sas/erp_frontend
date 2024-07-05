package com.kynsoft.finamer.settings.application.query.objectResponse.manageEmployee;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.manageEmployeeGroup.ManageEmployeeCountryClientAgencyDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageEmployeeCountryClientAgencyResponse implements IResponse {

    private String countryName;
    private List<ManageEmployeeClientAgencyResponse> clients;

    public ManageEmployeeCountryClientAgencyResponse(ManageEmployeeCountryClientAgencyDto dto){
        this.countryName = dto.getCountryName();
        this.clients = dto.getClients() != null ? dto.getClients().stream().map(ManageEmployeeClientAgencyResponse::new).collect(Collectors.toList()) : null;
    }
}

