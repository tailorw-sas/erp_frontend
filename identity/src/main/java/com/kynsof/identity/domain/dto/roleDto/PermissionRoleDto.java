package com.kynsof.identity.domain.dto.roleDto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
public class PermissionRoleDto {
    private UUID permissionId;
    private String code;
    private String description;
    private String action;

    public PermissionRoleDto(UUID permissionId, String code, String description, String action) {
        this.permissionId = permissionId;
        this.code = code;
        this.description = description;
        this.action = action;
    }

}