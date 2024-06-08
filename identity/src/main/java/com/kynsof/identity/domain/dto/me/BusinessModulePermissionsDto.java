package com.kynsof.identity.domain.dto.me;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BusinessModulePermissionsDto {
    private UUID businessId;
    private String name;
    private Set<PermissionInfo> uniquePermissions;
}
