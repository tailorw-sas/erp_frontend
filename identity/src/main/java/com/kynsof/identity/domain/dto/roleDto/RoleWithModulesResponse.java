package com.kynsof.identity.domain.dto.roleDto;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
@Getter
@Setter
public class RoleWithModulesResponse implements IResponse {
    private UUID id;
    private String name;
    private String description;
    private List<ModuleWithPermissionsDto> modules;

    public RoleWithModulesResponse(UUID id, String name, String description, List<ModuleWithPermissionsDto> modules) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.modules = modules;
    }


}
