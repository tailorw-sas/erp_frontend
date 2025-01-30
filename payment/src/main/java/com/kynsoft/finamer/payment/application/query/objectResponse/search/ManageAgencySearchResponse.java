package com.kynsoft.finamer.payment.application.query.objectResponse.search;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyDto;
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
public class ManageAgencySearchResponse implements IResponse {

    private UUID id;
    private String code;
    private String name;
    private ManageAgencyTypeSearchResponse agencyTypeResponse;
    private String status;

    public ManageAgencySearchResponse(ManageAgencyDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.agencyTypeResponse = dto.getAgencyType() != null ? new ManageAgencyTypeSearchResponse(dto.getAgencyType()) : null;
        this.status = dto.getStatus();
    }

    public ManageAgencySearchResponse(AgencyProjection agency) {
        this.id = agency.getId();
        this.code = agency.getCode();
        this.name = agency.getName();
        this.status = agency.getStatus();
        this.agencyTypeResponse = agency.getManageAgencyType() != null ? new ManageAgencyTypeSearchResponse(agency) : null;
    }
}
