package com.kynsoft.finamer.invoicing.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EGenerationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageAgencyResponse implements IResponse {

    private UUID id;
    private String code;
    private String name;
    private ManageClientResponse client;
    private EGenerationType generationType;

    public ManageAgencyResponse(ManageAgencyDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.client = dto.getClient() != null ? new ManageClientResponse(dto.getClient()) : null;
        this.generationType = dto.getGenerationType() != null ? dto.getGenerationType() : null;
    }

}
