package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageClientDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageClientResponse implements IResponse {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Status status;
    private List<ManageAgencyResponse> agencies;

    public ManageClientResponse(ManageClientDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.agencies = dto.getAgencies() != null ? dto.getAgencies().stream().map(ManageAgencyResponse::new).toList() : null;
    }

}
