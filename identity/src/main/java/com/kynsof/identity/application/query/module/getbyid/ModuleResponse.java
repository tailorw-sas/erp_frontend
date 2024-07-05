package com.kynsof.identity.application.query.module.getbyid;

import com.kynsof.identity.domain.dto.ModuleDto;
import com.kynsof.identity.domain.dto.PermissionDto;
import com.kynsof.identity.domain.dto.enumType.ModuleStatus;
import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class ModuleResponse implements IResponse {
    private UUID id;
    private String name;
    private String description;
    private String image;
    Set<PermissionDto> permissions = new HashSet<>();
    private ModuleStatus status;
    private String code;

    public ModuleResponse(ModuleDto object) {
        this.id = object.getId();
        this.name = object.getName();
        this.description = object.getDescription();
        this.image = object.getImage();
        permissions.addAll(object.getPermissions());
        this.status = object.getStatus();
        this.code = object.getCode();
    }

    public ModuleResponse(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public ModuleResponse(UUID id, String name, String code, ModuleStatus status){
        this.id = id;
        this.name = name;
        this.code = code;
        this.status = status;
    }

}