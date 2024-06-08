package com.kynsof.identity.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ModuleDto {
    protected UUID id;
    private String name;
    private String image;
    private String description;
    private List<PermissionDto> permissions = new ArrayList<>();
    private LocalDateTime createdAt;

    public ModuleDto(UUID id, String name, String image, String description, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.description = description;
        this.createdAt = createdAt;
    }
    public ModuleDto(UUID id, String name, String image, String description, List<PermissionDto> permissions) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.description = description;
        this.permissions = permissions;
    }

    public ModuleDto(UUID id, String name, String image, String description) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.description = description;
    }

}
