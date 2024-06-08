package com.kynsof.identity.application.query.permission.search;

import com.kynsof.identity.domain.dto.PermissionDto;
import com.kynsof.identity.domain.dto.enumType.PermissionStatusEnm;
import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PermissionSearchResponse implements IResponse {

    private UUID id;
    private String code;
    private String description;
    private PermissionStatusEnm status;
    private String action;
    private String moduleName;
    private LocalDate createdAt;
    public PermissionSearchResponse(PermissionDto response) {
        this.id = response.getId();
        this.code = response.getCode();
        this.description = response.getDescription();
        this.status = response.getStatus();
        this.action = response.getAction();
        this.moduleName = response.getModule().getName();
        this.createdAt = response.getCreatedAt().toLocalDate();
    }

}
