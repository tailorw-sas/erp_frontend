package com.kynsof.identity.domain.dto;

import com.kynsof.identity.domain.dto.enumType.RoleStatusEnm;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class RoleDto {
    private UUID id;
    private String name;
    private String description;
    private RoleStatusEnm status;
    private List<PermissionDto> permissionDtos;
    private boolean deleted;

    /**
     * Usar este constructor en le create y update
     * @param id
     * @param name
     * @param description
     * @param status 
     */
    public RoleDto(UUID id, String name, String description, RoleStatusEnm status, List<PermissionDto> permissionDtos) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.permissionDtos = permissionDtos;
    }

}
