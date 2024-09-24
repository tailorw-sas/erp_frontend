package com.kynsoft.finamer.payment.application.query.objectResponse.search;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyDto;
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

    public ManageAgencySearchResponse(ManageAgencyDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.agencyTypeResponse = dto.getAgencyType() != null ? new ManageAgencyTypeSearchResponse(dto.getAgencyType()) : null;
    }

}
