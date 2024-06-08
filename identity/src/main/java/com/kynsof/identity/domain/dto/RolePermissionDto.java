package com.kynsof.identity.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RolePermissionDto {

    private UUID id;
    private RoleDto role;
    private PermissionDto permission;
    private LocalDateTime deletedAt;
    private boolean deleted;
}
