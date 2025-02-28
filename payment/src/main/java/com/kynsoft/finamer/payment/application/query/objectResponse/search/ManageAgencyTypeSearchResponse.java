package com.kynsoft.finamer.payment.application.query.objectResponse.search;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyTypeDto;
import com.kynsoft.finamer.payment.infrastructure.identity.projection.AgencyProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageAgencyTypeSearchResponse implements IResponse {

    private UUID id;
    private String code;
    private String name;
    private String status;

    public ManageAgencyTypeSearchResponse(ManageAgencyTypeDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
    }

    public ManageAgencyTypeSearchResponse(AgencyProjection agency) {
        this.id = agency.getManageAgencyType().getId();
        this.code = agency.getManageAgencyType().getCode();
        this.name = agency.getManageAgencyType().getName();
        this.status = agency.getManageAgencyType().getStatus();
    }
}
