package com.kynsof.identity.application.query.permission.findAllGrouped;

import com.kynsof.identity.domain.dto.PermissionDto;
import com.kynsof.identity.domain.dto.enumType.PermissionStatusEnm;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PermissionBasicResponse {

    private UUID id;
    private String code;
    private PermissionStatusEnm status;
    private Boolean isHighRisk;
    private Boolean isIT;
    private String name;

    public PermissionBasicResponse(PermissionDto response){
        this.id = response.getId();
        this.code = response.getCode();
        this.status = response.getStatus();
        this.isHighRisk = response.getIsHighRisk();
        this.isIT = response.getIsIT();
        this.name = response.getName();
    }
}
