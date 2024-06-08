package com.kynsof.identity.application.query.permission.getById;

import com.kynsof.identity.application.query.module.getbyid.ModuleResponse;
import com.kynsof.identity.domain.dto.PermissionDto;
import com.kynsof.identity.domain.dto.enumType.PermissionStatusEnm;
import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PermissionResponse implements IResponse {

    private UUID id;
    private String code;
    private String description;
    private ModuleResponse module;
    private PermissionStatusEnm status;
    private String action;
    private LocalDateTime createdAt;

    public PermissionResponse(PermissionDto response) {
        this.id = response.getId();
        this.code = response.getCode();
        this.description = response.getDescription();
        this.module = new ModuleResponse(response.getModule().getId(),
                response.getModule().getName());
        this.status = response.getStatus();
        this.action = response.getAction();
        this.createdAt = response.getCreatedAt();
    }

}
