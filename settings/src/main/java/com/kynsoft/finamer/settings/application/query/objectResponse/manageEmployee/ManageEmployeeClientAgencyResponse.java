package com.kynsoft.finamer.settings.application.query.objectResponse.manageEmployee;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.application.query.objectResponse.manageAgencyGroup.ManageAgencyBasicResponse;
import com.kynsoft.finamer.settings.domain.dto.manageEmployeeGroup.ManageEmployeeClientAgencyDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ManageEmployeeClientAgencyResponse implements IResponse {
    private String clientName;
    private List<ManageAgencyBasicResponse> agencies;

    public ManageEmployeeClientAgencyResponse(ManageEmployeeClientAgencyDto dto){
        this.clientName = dto.getClientName();
        this.agencies = dto.getAgencies() != null ? dto.getAgencies().stream().map(ManageAgencyBasicResponse::new).collect(Collectors.toList()) : null;
    }
}
