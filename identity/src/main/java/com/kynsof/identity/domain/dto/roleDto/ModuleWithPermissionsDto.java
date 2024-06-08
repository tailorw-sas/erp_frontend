package com.kynsof.identity.domain.dto.roleDto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
@Getter
@Setter
public class ModuleWithPermissionsDto {
    private UUID id;
    private String name;
    private String description;
    private List<PermissionRoleDto> permissions;

    public ModuleWithPermissionsDto(UUID id, String name, String description, List<PermissionRoleDto> permissions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.permissions = permissions;
    }

    // Getters y setters
}