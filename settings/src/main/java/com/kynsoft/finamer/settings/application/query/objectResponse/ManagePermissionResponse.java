package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagePermissionDto;
import com.kynsoft.finamer.settings.domain.dto.PermissionDto;
import com.kynsoft.finamer.settings.domain.dto.PermissionStatusEnm;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.infrastructure.projections.ManagePermissionProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManagePermissionResponse implements IResponse {

    private UUID id;
    private String code;
    private PermissionStatusEnm status;
    private String description;
    private String name;
    private Boolean isHighRisk;

    public ManagePermissionResponse(PermissionDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
        this.description = dto.getDescription();
        this.name = dto.getName();
        this.isHighRisk = dto.getIsHighRisk();
    }

    public ManagePermissionResponse(ManagePermissionProjection dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
        this.description = dto.getDescription();
        this.name = dto.getName();
        this.isHighRisk = dto.getIsHighRisk();
    }
}
